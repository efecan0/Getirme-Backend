package com.example.getirme.service;

import com.example.getirme.dto.*;
import com.example.getirme.model.FileEntity;

import java.io.IOException;
import java.util.List;

public interface IRestaurantService {
void registerRestaurant(RestaurantDtoIU restaurantDtoIU);
void createProduct(ProductDtoIU product);
List<RestaurantDto> getRestaurantList();
RestaurantDetailsDto getRestaurantDetails(Long id);
ProductDetailsDto getProductDetails(Long id);

}
