package com.example.getirme.controller;

import com.example.getirme.dto.*;
import com.example.getirme.model.RootEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRestaurantController {
    ResponseEntity<RootEntity<String>> registerRestaurant(RestaurantDtoIU restaurant);

    ResponseEntity<RootEntity<String>> createProduct(String name,
                          String description,
                          Double price,
                          MultipartFile image,
                          String selectableContentJson);

    ResponseEntity<RootEntity<List<RestaurantDto>>> getRestaurantList();

    ResponseEntity<RootEntity<RestaurantDetailsDto>> getRestaurantDetails(Long id);

    ResponseEntity<RootEntity<ProductDetailsDto>> getProductDetails(Long id);

}
