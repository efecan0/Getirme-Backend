package com.example.getirme.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderDtoIU {
    private Long restaurantId;
    private List<OrderProductDtoIU> products;
}
