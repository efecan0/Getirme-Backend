package com.example.getirme.controller.impl;

import com.example.getirme.controller.IRestaurantController;
import com.example.getirme.dto.*;
import com.example.getirme.model.SelectableContentOption;
import com.example.getirme.service.IRestaurantService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restaurant")
public class RestaurantControllerImpl implements IRestaurantController {

    @Autowired
    IRestaurantService restaurantService;

    @PostMapping("/register")
    @Override
    public boolean registerRestaurant(@ModelAttribute RestaurantDtoIU restaurant) {
        return restaurantService.registerRestaurant(restaurant);
    }

    @PostMapping("/createProduct")
    @Override
    public boolean createProduct( @RequestParam("name") String name,
                                  @RequestParam("description") String description,
                                  @RequestParam("price") Double price,
                                  @RequestParam("image") MultipartFile image,
                                  @RequestParam("selectableContentOptionMap") String selectableContentOptionJson) {
        try {
            // JSON String'ini HashMap<String, List<SelectableContentOptionDtoIU>> tipine çevir
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<SelectableContentOptionDtoIU>> selectableContentOptionMap =
                    objectMapper.readValue(selectableContentOptionJson, new TypeReference<Map<String, List<SelectableContentOptionDtoIU>>>() {});
            ProductDtoIU productDto = new ProductDtoIU(name , description, price, image, selectableContentOptionMap);

            return restaurantService.createProduct(productDto);
        } catch (Exception e) {
            throw new RuntimeException("JSON Parse Hatası");
        }
    }

    @GetMapping("/list")
    @Override
    public List<RestaurantDto> getRestaurantList() {
        return restaurantService.getRestaurantList();
    }

    @GetMapping("/details/{id}")
    @Override
    public RestaurantDetailsDto getRestaurantDetails(@PathVariable Long id) {
        return restaurantService.getRestaurantDetails(id);
    }

    @GetMapping("/product/details/{id}")
    @Override
    public ProductDetailsDto getProductDetails(@PathVariable Long id) {
        return restaurantService.getProductDetails(id);
    }


}
