package com.example.getirme.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^5\\d{9}$", message = "Phone number must be 10 digits and start with 5")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be blank")
    private String password;

}
