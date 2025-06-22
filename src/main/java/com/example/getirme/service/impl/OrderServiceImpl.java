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
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private RestaurantRepository restaurantRepository;

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
    public OrderDto createOrder(OrderDtoIU orderDtoIU , Customer context) {
        Restaurant restaurant = restaurantRepository.findById(orderDtoIU.getRestaurantId()).orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Restaurant is not found.")));
        if(restaurant.getOpeningTime().isAfter(LocalTime.now()) && restaurant.getClosingTime().isBefore(LocalTime.now())){
            throw new BaseException(new ErrorMessage(BAD_REQUEST , "Outside of restaurant service hours"));
        }

        Double distance = openStreetMapService.calculateDistance(context.getLocation() , restaurant.getLocation());
        if(distance > restaurant.getMaxServiceDistance()){
            throw new BaseException(new ErrorMessage(BAD_REQUEST , "You are outside the restaurant service range."));
        }

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setCustomer(context);
        order.setRestaurant(restaurant);
        order.setDate(new Date());
        List<OrderProduct> orderProductList = new ArrayList<>();
        List<OrderSelectedContent> orderSelectedContentList = new ArrayList<>();

        for(OrderProductDtoIU orderProductDtoIU : orderDtoIU.getProducts() ){
            OrderProduct orderProduct = new OrderProduct();
            Product product = productRepository.findById(orderProductDtoIU.getProductId()).orElseThrow(()-> new BaseException(new ErrorMessage(NO_RECORD_EXIST , "Product not found.")));
            orderProduct.setName(product.getName());
            orderProduct.setSize(orderProductDtoIU.getSize());
            Double productPrice = orderProduct.getSize() * product.getPrice();
            order.sumPrice(productPrice);
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
                orderSelectedContentList.add(orderSelectedContent);
            }
            orderProduct.setSelectedContents(orderSelectedContentList);
            orderProductList.add(orderProduct);
        }

        if(order.getTotalPrice() < restaurant.getMinServicePricePerKm() * distance){
            throw new BaseException(new ErrorMessage(BAD_REQUEST , "Your Order is less then our minimum service price"));
        }

        orderSelectedContentRepository.saveAll(orderSelectedContentList);
        List<OrderProduct> savedOrderProductList = orderProductRepository.saveAll(orderProductList);
        order.setOrderProducts(savedOrderProductList);
        orderRepository.save(order);
        return convertOrderToDto(order);
    }

    public List<OrderDto> getMyOrders(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String userType = user.getUserType();
        List<Order> orders;
        List<OrderStatus> excludedStatuses = List.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED);

        if(userType.equals("RESTAURANT")) {
            orders = orderRepository.findActiveOrdersByRestaurantId(user.getId(), excludedStatuses)
                    .orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST, "No active orders found")));
        }else if(userType.equals("CUSTOMER")) {
                orders = orderRepository.findActiveOrdersByCustomerId(user.getId(), excludedStatuses)
                        .orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST, "No active orders found")));
            }
                else{
            throw new BaseException(new ErrorMessage(BAD_REQUEST , "User type can be customer or restaurant."));
        }

        return orders.stream()
                .map(this::convertOrderToDto)
                .toList();
    }

    private OrderDto convertOrderToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        Customer customer = order.getCustomer();
        Restaurant restaurant = order.getRestaurant();

        CustomerDto customerDto = new CustomerDto(customer.getName(), customer.getSurname(), customer.getLocation());
        orderDto.setCustomer(customerDto);

        if (restaurant != null) {
            Double distance = openStreetMapService.calculateDistance(customer.getLocation(), restaurant.getLocation());
            Double minServicePrice = distance * restaurant.getMinServicePricePerKm();

            RestaurantDto restaurantDto = new RestaurantDto();
            restaurantDto.setId(restaurant.getId());
            restaurantDto.setName(restaurant.getName());
            restaurantDto.setLocation(restaurant.getLocation());
            restaurantDto.setOpeningTime(restaurant.getOpeningTime());
            restaurantDto.setClosingTime(restaurant.getClosingTime());
            restaurantDto.setImage(fileEntityService.fileToByteArray(restaurant.getImage()));
            restaurantDto.setDistance(distance);
            restaurantDto.setMinServicePrice(minServicePrice);

            orderDto.setRestaurant(restaurantDto);
        }

        orderDto.setId(order.getId());
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setDate(order.getDate());
        orderDto.setStatus(order.getStatus());

        List<OrderProductDto> orderProductDtoList = convertOrderProductToDtoList(order);
        orderDto.setOrderProducts(orderProductDtoList);

        return orderDto;
    }


    private static List<OrderProductDto> convertOrderProductToDtoList(Order order) {
        List<OrderProductDto> orderProductDtoList = new ArrayList<>();
        for(OrderProduct orderProduct : order.getOrderProducts()){
            OrderProductDto orderProductDto = new OrderProductDto();
            orderProductDto.setId(orderProduct.getId());
            orderProductDto.setName(orderProduct.getName());
            orderProductDto.setSize(orderProduct.getSize());
            orderProductDtoList.add(orderProductDto);
        }
        return orderProductDtoList;
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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST, "Order not found")));

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.ON_THE_WAY) {
            order.setProgress(0);
        }

        orderRepository.save(order);

        String customerId = String.valueOf(order.getCustomer().getId());

        OrderStatusUpdateDto updateDto = new OrderStatusUpdateDto(order.getId(), newStatus, order.getProgress());
        messagingTemplate.convertAndSendToUser(customerId, "/queue/order-status", updateDto);
    }

    @Transactional
    public void updateProgress(Long orderId, Integer newProgress) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(NO_RECORD_EXIST, "Order not found")));

        order.setProgress(newProgress);
        orderRepository.save(order);

        String customerId = String.valueOf(order.getCustomer().getId());

        OrderStatusUpdateDto updateDto = new OrderStatusUpdateDto(order.getId(), null, newProgress);
        messagingTemplate.convertAndSendToUser(customerId, "/queue/order-status", updateDto);
    }

}