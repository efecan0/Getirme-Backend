package com.example.getirme.controller;

import com.example.getirme.dto.CustomerDto;
import com.example.getirme.dto.CustomerDtoIU;
import com.example.getirme.model.RootEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICustomerController {

    ResponseEntity<RootEntity<String>> register(CustomerDtoIU customerDto);
    ResponseEntity<RootEntity<String>> updateCustomer(CustomerDtoIU customerDto);
    ResponseEntity<RootEntity<CustomerDto>> getUserInfo();
}
