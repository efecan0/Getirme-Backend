package com.example.getirme.dto;

import com.example.getirme.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateDto {
    private Long orderId;
    private OrderStatus newStatus; // Opsiyonel (sadece progress güncelleniyorsa null kalabilir)
    private Integer progress;
}
