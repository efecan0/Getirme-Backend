package com.example.getirme.controller.impl;

import com.example.getirme.controller.IOrderController;
import com.example.getirme.dto.OrderDto;
import com.example.getirme.dto.OrderDtoIU;
import com.example.getirme.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderControllerImpl implements IOrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping("/createOrder")
    @Override
    public boolean createOrder(@RequestBody OrderDtoIU order) {
        return orderService.createOrder(order);
    }

    @GetMapping("/myOrders")
    @Override
    public List<OrderDto> getMyOrders(){
        return orderService.getMyOrders();
    }

    @GetMapping("/orderDetails/{id}")
    @Override
    public OrderDto getOrderDetails(@PathVariable Long id) {
        return orderService.getOrderDetails(id);
    }
}
