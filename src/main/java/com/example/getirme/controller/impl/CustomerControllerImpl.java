package com.example.getirme.controller.impl;

import com.example.getirme.controller.ICustomerController;
import com.example.getirme.dto.CustomerDtoIU;
import com.example.getirme.dto.RestaurantDto;
import com.example.getirme.model.RootEntity;
import com.example.getirme.service.ICustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerControllerImpl extends BaseController implements ICustomerController {

    @Autowired
    ICustomerService customerService;


    @PostMapping("/register")
    @Override
    public ResponseEntity<RootEntity<String>> register(@Valid @RequestBody CustomerDtoIU customerDto) {
        customerService.register(customerDto);
        return ok("Registered successfully.");
    }
}
