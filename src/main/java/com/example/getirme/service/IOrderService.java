package com.example.getirme.service;

import com.example.getirme.dto.OrderDto;
import com.example.getirme.dto.OrderDtoIU;
import com.example.getirme.model.Customer;
import com.example.getirme.model.OrderStatus;

import java.util.List;

public interface IOrderService {
    OrderDto createOrder(OrderDtoIU order , Customer context);
    List<OrderDto> getMyOrders();
    OrderDto getOrderDetails(Long id);

    void updateOrderStatus(Long orderId, OrderStatus newStatus);

    void updateProgress(Long orderId, Integer progress);
}
