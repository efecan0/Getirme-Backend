package com.example.getirme.controller;

import com.example.getirme.dto.*;
import com.example.getirme.model.RootEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRestaurantController {
    ResponseEntity<RootEntity<String>> registerRestaurant(@Valid RestaurantDtoIU restaurant);

    ResponseEntity<RootEntity<String>> createProduct(@NotBlank(message = "Name cannot be blank") String name,
                                                     @NotBlank(message = "Description cannot be blank") String description,
                                                     @NotNull(message = "Price cannot be null") @Positive(message = "Price must be greater than zero") Double price,
                                                     @NotNull(message = "Image cannot be null") MultipartFile image,
                                                     @NotBlank(message = "Selectable content JSON cannot be blank") String selectableContentJson);

    ResponseEntity<RootEntity<List<RestaurantDto>>> getRestaurantList();

    ResponseEntity<RootEntity<RestaurantDetailsDto>> getRestaurantDetails(Long id);

    ResponseEntity<RootEntity<ProductDetailsDto>> getProductDetails(Long id);

    ResponseEntity<RootEntity<String>> updateRestaurant(RestaurantDtoIU restaurantDtoIU);

    ResponseEntity<RootEntity<RestaurantDetailsDto>> getUserInfo();

    ResponseEntity<RootEntity<String>> updateProduct(UpdateProductDtoIU updateProductDtoIU,
                                                     MultipartFile image,
                                                     Long id);

}
