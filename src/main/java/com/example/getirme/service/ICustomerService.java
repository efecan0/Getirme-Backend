package com.example.getirme.service;

import com.example.getirme.dto.CustomerDtoIU;
import com.example.getirme.dto.LocationDto;
import com.example.getirme.dto.RestaurantDto;

import java.util.List;

public interface ICustomerService {
    void register(CustomerDtoIU customerDtoIU);
    LocationDto getCustomerLocation();

}
