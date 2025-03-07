package com.example.getirme.controller.impl;

import com.example.getirme.controller.IOrderController;
import com.example.getirme.dto.OrderDto;
import com.example.getirme.dto.OrderDtoIU;
import com.example.getirme.model.RootEntity;
import com.example.getirme.service.IOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderControllerImpl extends BaseController implements IOrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping("/createOrder")
    @Override
    public ResponseEntity<RootEntity<String>> createOrder(@Valid @RequestBody OrderDtoIU order) {
        orderService.createOrder(order);
        return ok("Order Created Successfully");
    }

    @GetMapping("/myOrders")
    @Override
    public ResponseEntity<RootEntity<List<OrderDto>>> getMyOrders(){
        List<OrderDto> response = orderService.getMyOrders();
        return ok(response);
    }

    @GetMapping("/orderDetails/{id}")
    @Override
    public ResponseEntity<RootEntity<OrderDto>> getOrderDetails(@PathVariable Long id) {
        OrderDto response = orderService.getOrderDetails(id);
        return ok(response);
    }
}
