package com.example.getirme.controller;

import com.example.getirme.dto.OrderDto;
import com.example.getirme.dto.OrderDtoIU;
import com.example.getirme.model.RootEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderController {
    ResponseEntity<RootEntity<String>> createOrder(OrderDtoIU order);
    ResponseEntity<RootEntity<List<OrderDto>>> getMyOrders();
    ResponseEntity<RootEntity<OrderDto>> getOrderDetails(Long id);
}
