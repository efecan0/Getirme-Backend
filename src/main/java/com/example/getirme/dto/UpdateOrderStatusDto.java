package com.example.getirme.dto;

import com.example.getirme.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusDto {
    private Long orderId;
    private OrderStatus newStatus;
}
