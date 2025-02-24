package com.example.getirme.service.impl;

import com.example.getirme.dto.*;
import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import com.example.getirme.jwt.JwtService;
import com.example.getirme.model.*;
import com.example.getirme.repository.ProductRepository;
import com.example.getirme.repository.RestaurantRepository;
import com.example.getirme.repository.SelectableContentOptionRepository;
import com.example.getirme.repository.SelectableContentRepository;
import com.example.getirme.service.IFileEntityService;
import com.example.getirme.service.IRestaurantService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.getirme.exception.MessageType.*;

@Service
public class RestaurantServiceImpl implements IRestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private IFileEntityService fileEntityService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SelectableContentOptionRepository selectableContentOptionRepository;

    @Autowired
    private SelectableContentRepository selectableContentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OpenStreetMapService openStreetMapService;

    @Autowired
    private JwtService jwtService;

    @Override
    public void registerRestaurant(RestaurantDtoIU restaurantDtoIU){

        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDtoIU.getName());
        restaurant.setPhoneNumber(restaurantDtoIU.getPhoneNumber().replaceAll(" " , ""));
        restaurant.setLocation(restaurantDtoIU.getLocation());
        restaurant.setOpeningTime(restaurantDtoIU.getOpeningTime());
        restaurant.setClosingTime(restaurantDtoIU.getClosingTime());
        restaurant.setPassword(passwordEncoder.encode(restaurantDtoIU.getPassword()));
        restaurant.setMaxServiceDistance(restaurantDtoIU.getMaxServiceDistance());
        restaurant.setMinServicePricePerKm(restaurantDtoIU.getMinServicePricePerKm());
        FileEntity fileEntity = fileEntityService.saveFileEntity(restaurantDtoIU.getImage());
        restaurant.setImage(fileEntity);
        restaurantRepository.save(restaurant);
    }


    @Override
    public void createProduct(ProductDtoIU productDtoIU) {
        try{
            User context = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
            if(context.getUserType().equals("RESTAURANT")){
                Product product = new Product();
                product.setName(productDtoIU.getName());
                product.setDescription(productDtoIU.getDescription());
                product.setPrice(productDtoIU.getPrice());

                if(productDtoIU.getImage() != null){
                    FileEntity fileEntity = fileEntityService.saveFileEntity(productDtoIU.getImage());
                    product.setImage(fileEntity);
                }

                // If we take selectable content and option Map like
                // "Extra grammage selection": [{name : "+30g" , price : 2 } , {name : "+50g" , price: 3}] comes into the situation
                // SelectableContent is key and SelectableContentOption is a object in list.
                if(productDtoIU.getSelectableContentOptions() != null){
                    List<SelectableContent> selectableContentList = new ArrayList<>();

                    //loop all keys (SelectableContent names)
                    for(String key : productDtoIU.getSelectableContentOptions().keySet() ){
                        List<SelectableContentOption> selectableContentOptionList = new ArrayList<>();
                        //loop List of SelectableContentOptions for SelectableContent names
                        for(SelectableContentOptionDtoIU value : productDtoIU.getSelectableContentOptions().get(key)){
                            //if we have a same SelectableContentOption in database just add to selectableContentOptionList.
                            //else save database and add list.
                            Optional<SelectableContentOption> optional = selectableContentOptionRepository.findByNameAndPrice(value.getName() , value.getPrice());
                            if(optional.isPresent()){
                                selectableContentOptionList.add(optional.get());
                            }else{
                                SelectableContentOption savedOption =  selectableContentOptionRepository.save(new SelectableContentOption(null , value.getName(), value.getPrice()));
                                selectableContentOptionList.add(savedOption);
                            }
                        }

                        SelectableContent savedSelectableContent = selectableContentRepository.save(new SelectableContent(null , key , selectableContentOptionList));
                        selectableContentList.add(savedSelectableContent);

                    }
                    //set selectedContentList the product
                    product.setSelectableContents(selectableContentList);
                }
                //We have full of selectableContentList in Product object. and in this list per item have a SelectableContentOptionList.
                //Now can save the product on database
                Product savedProduct = productRepository.save(product);
                Restaurant restaurant =  restaurantRepository.findById(context.getId()).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Restaurant not found")));
                restaurant.addProduct(savedProduct);
                restaurantRepository.save(restaurant);
            }
        }catch (Exception e){
            throw new BaseException(new ErrorMessage(GENERAL_ERROR , "Error creating product"));
        }
    }

    @Override
    public List<RestaurantDto> getRestaurantList() {
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        List<RestaurantDto> restaurantDtoList = new ArrayList<>();
        User context = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        for(Restaurant restaurant : restaurantList){
            Double distance = openStreetMapService.calculateDistance(context.getLocation() , restaurant.getLocation());
            if(distance <= restaurant.getMaxServiceDistance()){
                byte[] restaurantImage = fileEntityService.fileToByteArray(restaurant.getImage());
                Double minServicePrice = distance * restaurant.getMinServicePricePerKm();
                RestaurantDto restaurantDto = new RestaurantDto(restaurant.getId(), restaurant.getName() , restaurant.getLocation() , restaurant.getOpeningTime() , restaurant.getClosingTime() , restaurantImage , distance , minServicePrice);
                restaurantDtoList.add(restaurantDto);
            }

        }


        return restaurantDtoList;

    }

    @Override
    public RestaurantDetailsDto getRestaurantDetails(Long id){
        User context = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Restaurant not found")));
        RestaurantDetailsDto restaurantDetailsDto = new RestaurantDetailsDto();
        byte[] restaurantImage = fileEntityService.fileToByteArray(restaurant.getImage());
        Double distance = openStreetMapService.calculateDistance(context.getLocation() , restaurant.getLocation());
        Double minServicePrice = distance * restaurant.getMinServicePricePerKm();

        RestaurantDto restaurantDto = new RestaurantDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getLocation(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                restaurantImage,
                distance,
                minServicePrice );

        restaurantDetailsDto.setRestaurant(restaurantDto);
        List<ProductDto> productDtoList = new ArrayList<>();
        for(Product product : restaurant.getProducts()){
            byte[] productImage = fileEntityService.fileToByteArray(product.getImage());
            ProductDto productDto = new ProductDto( product.getId() , product.getName() , product.getDescription() , product.getPrice() , productImage);
            productDtoList.add(productDto);
        }
        restaurantDetailsDto.setProducts(productDtoList);
        return restaurantDetailsDto;
    }

    @Override
    public ProductDetailsDto getProductDetails(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Product not found")));
        ProductDetailsDto productDetailsDto = new ProductDetailsDto();
        ProductDto productDto = new ProductDto( product.getId(), product.getName() , product.getDescription() , product.getPrice() , fileEntityService.fileToByteArray(product.getImage()));
        productDetailsDto.setProduct(productDto);
        List<SelectableContentDto> selectableContentDtoList = new ArrayList<>();
        for(SelectableContent selectableContent : product.getSelectableContents()){
            SelectableContentDto selectableContentDto = getSelectableContentDto(selectableContent);
            selectableContentDtoList.add(selectableContentDto);
        }
        productDetailsDto.setSelectableContent(selectableContentDtoList);
        return productDetailsDto;
    }

    private static SelectableContentDto getSelectableContentDto(SelectableContent selectableContent) {
        SelectableContentDto selectableContentDto = new SelectableContentDto();
        selectableContentDto.setId(selectableContent.getId());
        selectableContentDto.setName(selectableContent.getName());

        List<SelectableContentOptionDto> selectableContentOptionDtoList = new ArrayList<>();
        for(SelectableContentOption selectableContentOption : selectableContent.getOptions()){
            SelectableContentOptionDto selectableContentOptionDto = new SelectableContentOptionDto( selectableContentOption.getId() , selectableContentOption.getName() , selectableContentOption.getPrice());
            selectableContentOptionDtoList.add(selectableContentOptionDto);
        }
        selectableContentDto.setSelectableContentOptionDtoList(selectableContentOptionDtoList);
        return selectableContentDto;
    }


}
