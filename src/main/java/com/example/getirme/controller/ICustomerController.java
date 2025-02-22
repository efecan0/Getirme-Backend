package com.example.getirme.controller;

import com.example.getirme.dto.CustomerDtoIU;
import com.example.getirme.dto.RestaurantDto;

import java.util.List;

public interface ICustomerController {

    boolean register(CustomerDtoIU customerDto);
}
