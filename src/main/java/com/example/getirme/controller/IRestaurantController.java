package com.example.getirme.controller;

import com.example.getirme.dto.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRestaurantController {
    boolean registerRestaurant(RestaurantDtoIU restaurant);

    boolean createProduct(String name,
                          String description,
                          Double price,
                          MultipartFile image,
                          String selectableContentJson);

    List<RestaurantDto> getRestaurantList();

    RestaurantDetailsDto getRestaurantDetails(Long id);

    ProductDetailsDto getProductDetails(Long id);

}
