package com.example.getirme.controller.impl;

import com.example.getirme.controller.ICustomerController;
import com.example.getirme.dto.CustomerDtoIU;
import com.example.getirme.dto.LocationDto;
import com.example.getirme.dto.RestaurantDto;
import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import com.example.getirme.model.RootEntity;
import com.example.getirme.service.ICustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.getirme.exception.MessageType.UNAUTHORIZED;

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

    @GetMapping("/location")
    public ResponseEntity<RootEntity<LocationDto>> getCustomerLocation() {
        return ok(customerService.getCustomerLocation());
    }
}
