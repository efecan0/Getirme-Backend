package com.example.getirme.service;

import com.example.getirme.dto.CustomerDtoIU;
import com.example.getirme.dto.RestaurantDto;

import java.util.List;

public interface ICustomerService {
    boolean register(CustomerDtoIU customerDtoIU);
}
