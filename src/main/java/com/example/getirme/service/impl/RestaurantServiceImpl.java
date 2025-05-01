package com.example.getirme.service.impl;

import com.example.getirme.dto.*;
import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import com.example.getirme.model.*;
import com.example.getirme.repository.*;
import com.example.getirme.service.IFileEntityService;
import com.example.getirme.service.IRestaurantService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
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
    private FileEntityRepository fileEntityRepository;

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
                                SelectableContentOption savedOption =  selectableContentOptionRepository.save(new SelectableContentOption(null , value.getName(), value.getPrice()));
                                selectableContentOptionList.add(savedOption);
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
    @Transactional
    @Override
    public void updateRestaurant(RestaurantDtoIU restaurantDtoIU , Long id) {
        Restaurant context = (Restaurant) SecurityContextHolder.getContext().getAuthentication().getDetails();

        if(restaurantDtoIU.getPhoneNumber() != null){
            restaurantDtoIU.setPhoneNumber(restaurantDtoIU.getPhoneNumber().replaceAll(" " , ""));
        } else{
            restaurantDtoIU.setPhoneNumber(context.getPhoneNumber());
        }

        if(restaurantDtoIU.getPassword() != null){
            restaurantDtoIU.setPassword(passwordEncoder.encode(restaurantDtoIU.getPassword()));
        } else{
            restaurantDtoIU.setPassword(context.getPassword());
        }

        FileEntity image;
        if(restaurantDtoIU.getImage() != null){
            image = fileEntityService.saveFileEntity(restaurantDtoIU.getImage());
            fileEntityService.deleteFileFromDisk(context.getImage());
        } else {
            image = context.getImage();
        }

        restaurantRepository.updateRestaurant(
                context.getId(),
                restaurantDtoIU.getName(),
                restaurantDtoIU.getPhoneNumber(),
                restaurantDtoIU.getLocation(),
                restaurantDtoIU.getPassword(),
                restaurantDtoIU.getOpeningTime(),
                restaurantDtoIU.getClosingTime(),
                restaurantDtoIU.getMaxServiceDistance(),
                restaurantDtoIU.getMinServicePricePerKm(),
                image
        );
    }

    @Override
    public void updateProduct(UpdateProductDtoIU productDtoIU , MultipartFile image , Long id) {
        Product product = new Product();
        if(image != null){
            product.setImage(fileEntityService.saveFileEntity(image));
        }
        else{
            Product dbProduct = productRepository.findById(id).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Product not found")));
            product.setImage(dbProduct.getImage());
        }
        product.setId(id);
        product.setName(productDtoIU.getName());
        product.setDescription(productDtoIU.getDescription());
        product.setPrice(productDtoIU.getPrice());
        List<SelectableContent> selectableContentList = new ArrayList<>();
        for(SelectableContentDto selectableContentDto : productDtoIU.getSelectableContentDtoList()){
            SelectableContent selectableContent = new SelectableContent();
            selectableContent.setId(selectableContentDto.getId());
            selectableContent.setName(selectableContentDto.getName());
            List<SelectableContentOption> selectableContentOptionList = new ArrayList<>();
            for(SelectableContentOptionDto selectableContentOptionDto : selectableContentDto.getSelectableContentOptionDtoList()){
                SelectableContentOption selectableContentOption = new SelectableContentOption();
                selectableContentOption.setId(selectableContentOptionDto.getId());
                selectableContentOption.setName(selectableContentOptionDto.getName());
                selectableContentOption.setPrice(selectableContentOptionDto.getPrice());
                selectableContentOptionList.add(selectableContentOption);
            }
            List<SelectableContentOption> savedOptions = selectableContentOptionRepository.saveAll(selectableContentOptionList);
            selectableContent.setOptions(savedOptions);
            selectableContentList.add(selectableContent);
        }
        List<SelectableContent> savedSelectableContents = selectableContentRepository.saveAll(selectableContentList);
        product.setSelectableContents(savedSelectableContents);

        productRepository.save(product);
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

    @Override
    public void deleteProduct(Long id){
        Restaurant context = (Restaurant) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Product product =  productRepository.findById(id).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Product not found")));
        Restaurant restaurant = restaurantRepository.findById(context.getId()).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Restaurant not found")));

        restaurant.getProducts().remove(product);
        restaurantRepository.save(restaurant);

        FileEntity fileEntity = product.getImage();


        if(fileEntity != null){
            product.setImage(null);
            fileEntityRepository.delete(fileEntity);
            fileEntityService.deleteFileFromDisk(fileEntity);
        }
        for(SelectableContent selectableContent : product.getSelectableContents()){
            List<SelectableContentOption> options = new ArrayList<>(selectableContent.getOptions());
            selectableContent.getOptions().clear();
            selectableContentOptionRepository.deleteAll(options);
        }
        List<SelectableContent> selectableContents = new ArrayList<>(product.getSelectableContents());
        product.getSelectableContents().clear();
        selectableContentRepository.deleteAll(selectableContents);
        productRepository.delete(product);

    }


}
