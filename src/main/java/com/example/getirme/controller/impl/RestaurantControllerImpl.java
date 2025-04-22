package com.example.getirme.controller.impl;

import com.example.getirme.controller.IRestaurantController;
import com.example.getirme.dto.*;
import com.example.getirme.model.RootEntity;
import com.example.getirme.model.SelectableContentOption;
import com.example.getirme.service.IRestaurantService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restaurant")
@Validated
public class RestaurantControllerImpl extends BaseController implements IRestaurantController {

    @Autowired
    IRestaurantService restaurantService;

    @PostMapping("/register")
    @Override
    public ResponseEntity<RootEntity<String>> registerRestaurant( @ModelAttribute RestaurantDtoIU restaurant) {
        restaurantService.registerRestaurant(restaurant);
        return ok("Registered Successfully.");
    }

    @PostMapping("/createProduct")
    @Override
    public ResponseEntity<RootEntity<String>> createProduct( @RequestParam("name") String name,
                                                             @RequestParam("description")String description,
                                                             @RequestParam("price") Double price,
                                                             @RequestParam("image") MultipartFile image,
                                                             @RequestParam("selectableContentOptionMap") String selectableContentOptionJson) {  
        try {
            // JSON String'ini HashMap<String, List<SelectableContentOptionDtoIU>> tipine çevir
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<SelectableContentOptionDtoIU>> selectableContentOptionMap =
                    objectMapper.readValue(selectableContentOptionJson, new TypeReference<Map<String, List<SelectableContentOptionDtoIU>>>() {});
            ProductDtoIU productDto = new ProductDtoIU(name , description, price, image, selectableContentOptionMap);

            restaurantService.createProduct(productDto);
            return ok("Product Successfully Created.");
        } catch (Exception e) {
            return error("JSON Parse Error." , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    @Override
    public ResponseEntity<RootEntity<List<RestaurantDto>>> getRestaurantList() {
        List<RestaurantDto> response = restaurantService.getRestaurantList();
        return ok(response);
    }

    @GetMapping("/details/{id}")
    @Override
    public ResponseEntity<RootEntity<RestaurantDetailsDto>> getRestaurantDetails(@PathVariable Long id) {
        RestaurantDetailsDto response = restaurantService.getRestaurantDetails(id);
        return ok(response);
    }

    @GetMapping("/product/details/{id}")
    @Override
    public ResponseEntity<RootEntity<ProductDetailsDto>> getProductDetails(@PathVariable Long id) {
        return ok(restaurantService.getProductDetails(id));
    }


}
