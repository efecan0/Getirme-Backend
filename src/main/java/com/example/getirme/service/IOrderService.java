package com.example.getirme.service;

import com.example.getirme.dto.OrderDto;
import com.example.getirme.dto.OrderDtoIU;

import java.util.List;

public interface IOrderService {
    void createOrder(OrderDtoIU order);
    List<OrderDto> getMyOrders();
    OrderDto getOrderDetails(Long id);
}
