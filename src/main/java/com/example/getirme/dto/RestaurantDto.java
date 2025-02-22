package com.example.getirme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {
    private Long id;
    private String name;
    private String location;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private byte[] image;
    private Double distance;
    private Double minServicePrice;
}
