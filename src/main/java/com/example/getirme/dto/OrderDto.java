package com.example.getirme.dto;

import com.example.getirme.model.Customer;
import com.example.getirme.model.OrderProduct;
import com.example.getirme.model.Restaurant;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;

    private Double totalPrice = 0.0;

    private Date date;

    private CustomerDto customer;

    private RestaurantDto restaurant;

    private List<OrderProductDto> orderProducts;
}
