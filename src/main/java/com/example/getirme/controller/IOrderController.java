package com.example.getirme.controller;

import com.example.getirme.dto.OrderDto;
import com.example.getirme.dto.OrderDtoIU;

import java.util.List;

public interface IOrderController {
    boolean createOrder(OrderDtoIU order);
    public List<OrderDto> getMyOrders();
    OrderDto getOrderDetails(Long id);
}
