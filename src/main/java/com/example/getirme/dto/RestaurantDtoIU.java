package com.example.getirme.dto;

import com.example.getirme.model.FileEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDtoIU {

    @NotBlank(message = "Restaurant name cannot be blank")
    private String name;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "5[0-9]{9}", message = "Phone number must be 10 digits and start with 5")
    private String phoneNumber;

    @NotBlank(message = "Location cannot be blank")
    private String location;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).{8,}$",
            message = "Password must be at least 8 characters long and contain at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character."
    )
    private String password;

    @NotNull(message = "Opening time cannot be null")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openingTime;

    @NotNull(message = "Closing time cannot be null")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closingTime;

    @NotNull(message = "Image cannot be null")
    private MultipartFile image;

    @NotNull(message = "Max service distance cannot be null")
    @Min(value = 1, message = "Max service distance must be at least 1 km")
    private Integer maxServiceDistance;

    @NotNull(message = "Min service price per km cannot be null")
    @Min(value = 0, message = "Min service price per km cannot be negative")
    private Integer minServicePricePerKm;
}

