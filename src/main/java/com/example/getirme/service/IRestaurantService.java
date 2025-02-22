package com.example.getirme.service;

import com.example.getirme.dto.*;
import com.example.getirme.model.FileEntity;

import java.io.IOException;
import java.util.List;

public interface IRestaurantService {
boolean registerRestaurant(RestaurantDtoIU restaurantDtoIU);
boolean createProduct(ProductDtoIU product);
List<RestaurantDto> getRestaurantList();
RestaurantDetailsDto getRestaurantDetails(Long id);
ProductDetailsDto getProductDetails(Long id);

}
