package com.example.getirme.service.impl;

import com.example.getirme.dto.*;
import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import com.example.getirme.model.*;
import com.example.getirme.repository.*;
import com.example.getirme.service.IFileEntityService;
import com.example.getirme.service.IOrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.example.getirme.exception.MessageType.BAD_REQUEST;
import static com.example.getirme.exception.MessageType.NO_RECORD_EXIST;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private SelectableContentRepository selectableContentRepository;

    @Autowired
    private SelectableContentOptionRepository selectableContentOptionRepository;

    @Autowired
    private OrderSelectedContentRepository orderSelectedContentRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IFileEntityService fileEntityService;

    @Autowired
    private OpenStreetMapService openStreetMapService;

    @Transactional
    @Override
    public void createOrder(OrderDtoIU orderDtoIU) {
        Customer context = (Customer) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Restaurant restaurant = restaurantRepository.findById(orderDtoIU.getRestaurantId()).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Restaurant is not found.")));
        if(!context.getUserType().equals("CUSTOMER")){
            throw new BaseException(new ErrorMessage(BAD_REQUEST , "User should be a customer"));
        }
        if(restaurant.getOpeningTime().isAfter(LocalTime.now()) && restaurant.getClosingTime().isBefore(LocalTime.now())){
            throw new BaseException(new ErrorMessage(BAD_REQUEST , "Outside of restaurant service hours"));
        }

        Order order = new Order();
        order.setCustomer(context);
        order.setRestaurant(restaurant);
        order.setDate(new Date());
        List<OrderProduct> orderProductList = new ArrayList<>();

        for(OrderProductDtoIU orderProductDtoIU : orderDtoIU.getProducts() ){
            OrderProduct orderProduct = new OrderProduct();
            Product product = productRepository.findById(orderProductDtoIU.getProductId()).orElseThrow(()-> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Product not found.")));
            orderProduct.setName(product.getName());
            orderProduct.setSize(orderProductDtoIU.getSize());
            Double productPrice = orderProduct.getSize() * product.getPrice();
            order.sumPrice(productPrice);
            List<OrderSelectedContent> orderSelectedContentList = new ArrayList<>();
            Map<Long , List<Long>> selectedContentMap = orderProductDtoIU.getSelectableContentMap();
            for(Long selectedContentId : selectedContentMap.keySet() ){

                SelectableContent selectableContent = product.getSelectableContents()
                        .stream()
                        .filter(obj -> selectedContentId == obj.getId())
                        .findFirst()
                        .orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Selectable Content Not Found")));

                OrderSelectedContent orderSelectedContent = new OrderSelectedContent();
                orderSelectedContent.setName(selectableContent.getName());
                List<SelectableContentOption> orderSelectedContentOptions = new ArrayList<>();
                for(Long selectedContentOptionId : selectedContentMap.get(selectedContentId)){

                    SelectableContentOption selectableContentOption = selectableContent.getOptions()
                            .stream()
                            .filter(obj -> selectedContentOptionId == obj.getId())
                            .findFirst()
                            .orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Option Not Found")));

                    orderSelectedContentOptions.add(selectableContentOption);
                    order.sumPrice(selectableContentOption.getPrice());
                }
                orderSelectedContent.setOrderOptions(orderSelectedContentOptions);
                OrderSelectedContent savedOrderSelectedContent = orderSelectedContentRepository.save(orderSelectedContent);
                orderSelectedContentList.add(savedOrderSelectedContent);
            }
            orderProduct.setSelectedContents(orderSelectedContentList);
            OrderProduct savedOrderProduct = orderProductRepository.save(orderProduct);
            orderProductList.add(savedOrderProduct);
        }

        order.setOrderProducts(orderProductList);
        orderRepository.save(order);
    }

    public List<OrderDto> getMyOrders(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String userType = user.getUserType();
        List<Order> orders;
        if(userType.equals("RESTAURANT")){
            orders = orderRepository.findByRestaurantId(user.getId()).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Restaurant not found")));
        }
        else if(userType.equals("CUSTOMER")){
            orders = orderRepository.findByCustomerId(user.getId()).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Customer not found")));
        }
        else{
            throw new BaseException(new ErrorMessage(BAD_REQUEST , "User type can be customer or restaurant"));
        }

        List<OrderDto> orderDtoList = new ArrayList<>();
        for(Order order : orders){
            OrderDto orderDto = new OrderDto();
            Customer customer = order.getCustomer();
            Restaurant restaurant = order.getRestaurant();
            CustomerDto customerDto = new CustomerDto(customer.getName() , customer.getSurname() , customer.getLocation());
            Double distance = openStreetMapService.calculateDistance(customer.getLocation() , restaurant.getLocation());
            RestaurantDto restaurantDto = new RestaurantDto();
            if(distance <= restaurant.getMaxServiceDistance()){
                Double minServicePrice = distance * restaurant.getMinServicePricePerKm();
                restaurantDto.setId(restaurant.getId());
                restaurantDto.setName(restaurant.getName());
                restaurantDto.setLocation(restaurant.getLocation());
                restaurantDto.setOpeningTime(restaurant.getOpeningTime());
                restaurantDto.setClosingTime(restaurant.getClosingTime());
                restaurantDto.setImage(fileEntityService.fileToByteArray(restaurant.getImage()));
                restaurantDto.setDistance(distance);
                restaurantDto.setMinServicePrice(minServicePrice);
            }


            orderDto.setCustomer(customerDto);
            orderDto.setRestaurant(restaurantDto);
            orderDto.setId(order.getId());
            orderDto.setTotalPrice(order.getTotalPrice());
            orderDto.setDate(order.getDate());
            List<OrderProductDto> orderProductDtoList = new ArrayList<>();
            for(OrderProduct orderProduct : order.getOrderProducts()){
                OrderProductDto orderProductDto = new OrderProductDto();
                orderProductDto.setId(orderProduct.getId());
                orderProductDto.setName(orderProduct.getName());
                orderProductDto.setSize(orderProduct.getSize());
                orderProductDtoList.add(orderProductDto);
            }

            orderDto.setOrderProducts(orderProductDtoList);
            orderDtoList.add(orderDto);
        }
        return orderDtoList;
    }

    @Override
    public OrderDto getOrderDetails(Long id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Order not found")));
        OrderDto orderDto = new OrderDto();

        Customer customer = order.getCustomer();
        Restaurant restaurant = order.getRestaurant();

        CustomerDto customerDto = new CustomerDto(
                customer.getName(),
                customer.getSurname(),
                customer.getLocation()
        );
        Double distance = openStreetMapService.calculateDistance(customer.getLocation() , restaurant.getLocation());
        RestaurantDto restaurantDto = new RestaurantDto();
        if(distance <= restaurant.getMaxServiceDistance()){
            Double minServicePrice = distance * restaurant.getMinServicePricePerKm();
            restaurantDto.setId(restaurant.getId());
            restaurantDto.setName(restaurant.getName());
            restaurantDto.setLocation(restaurant.getLocation());
            restaurantDto.setOpeningTime(restaurant.getOpeningTime());
            restaurantDto.setClosingTime(restaurant.getClosingTime());
            restaurantDto.setImage(fileEntityService.fileToByteArray(restaurant.getImage()));
            restaurantDto.setDistance(distance);
            restaurantDto.setMinServicePrice(minServicePrice);
        }


        orderDto.setId(order.getId());
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setDate(order.getDate());
        orderDto.setCustomer(customerDto);
        orderDto.setRestaurant(restaurantDto);

        List<OrderProductDto> orderProductDtoList = new ArrayList<>();
        for(OrderProduct orderProduct : order.getOrderProducts()){
            OrderProductDto orderProductDto = getOrderProductDto(orderProduct);
            orderProductDtoList.add(orderProductDto);
        }
        orderDto.setOrderProducts(orderProductDtoList);
        return orderDto;
    }

    private static OrderProductDto getOrderProductDto(OrderProduct orderProduct) {
        OrderProductDto orderProductDto = new OrderProductDto();
        orderProductDto.setName(orderProduct.getName());
        orderProductDto.setSize(orderProduct.getSize());
        orderProductDto.setId(orderProduct.getId());
        List<SelectableContentDto> selectableContentDtoList = new ArrayList<>();
        for(OrderSelectedContent selectableContent : orderProduct.getSelectedContents()){
            SelectableContentDto selectableContentDto = getSelectableContentDto(selectableContent);
            selectableContentDtoList.add(selectableContentDto);
        }
        orderProductDto.setSelectableContentDtoList(selectableContentDtoList);
        return orderProductDto;
    }

    private static SelectableContentDto getSelectableContentDto(OrderSelectedContent selectableContent) {
        SelectableContentDto selectableContentDto = new SelectableContentDto();
        selectableContentDto.setId(selectableContent.getId());
        selectableContentDto.setName(selectableContent.getName());
        List<SelectableContentOptionDto> selectableContentOptionList = new ArrayList<>();
        for( SelectableContentOption selectableContentOption : selectableContent.getOrderOptions()){
            SelectableContentOptionDto selectableContentOptionDto = new SelectableContentOptionDto(
                    selectableContentOption.getId(),
                    selectableContentOption.getName(),
                    selectableContentOption.getPrice()
            );
            selectableContentOptionList.add(selectableContentOptionDto);
        }
        selectableContentDto.setSelectableContentOptionDtoList(selectableContentOptionList);
        return selectableContentDto;
    }

}
