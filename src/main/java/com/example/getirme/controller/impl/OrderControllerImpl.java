package com.example.getirme.controller.impl;

import com.example.getirme.controller.IOrderController;
import com.example.getirme.dto.OrderDto;
import com.example.getirme.dto.OrderDtoIU;
import com.example.getirme.dto.UpdateOrderStatusDto;
import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import com.example.getirme.model.Customer;
import com.example.getirme.model.Order;
import com.example.getirme.model.RootEntity;
import com.example.getirme.service.IOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.getirme.exception.MessageType.BAD_REQUEST;

@RestController
public class OrderControllerImpl extends BaseController implements IOrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @PostMapping("/createOrder")
    @Override
    public ResponseEntity<RootEntity<String>> createOrder(@Valid @RequestBody OrderDtoIU order) {

        Customer context = (Customer) SecurityContextHolder.getContext().getAuthentication().getDetails();

        if(!context.getUserType().equals("CUSTOMER")){
            throw new BaseException(new ErrorMessage(BAD_REQUEST , "User should be a customer"));
        }
        OrderDto savedOrder = orderService.createOrder(order , context);
        String userId = String.valueOf(order.getRestaurantId());
        messagingTemplate.convertAndSendToUser(userId , "/queue/order-status" , savedOrder);
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

    @MessageMapping("/updateOrderStatus")
    public void updateOrderStatus(UpdateOrderStatusDto dto) {
        orderService.updateOrderStatus(dto.getOrderId(), dto.getNewStatus());
    }

    @PutMapping("/progress")
    public ResponseEntity<RootEntity<String>> updateOrderProgress(@RequestParam Long orderId,
                                                                  @RequestParam Integer progress) {
        orderService.updateProgress(orderId, progress);
        return ok("Order progress updated successfully.");
    }


}
