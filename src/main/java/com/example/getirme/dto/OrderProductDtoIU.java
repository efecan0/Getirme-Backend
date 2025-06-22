package com.example.getirme.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDtoIU {

    @NotNull(message = "Size cannot be null")
    private Long size;

    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    private Map<Long, List<Long>> selectableContentMap;
}

