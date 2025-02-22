package com.example.getirme.controller.impl;

import com.example.getirme.controller.IRefreshTokenController;
import com.example.getirme.jwt.AuthResponse;
import com.example.getirme.service.IRefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefreshTokenImpl implements IRefreshTokenController {

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @PostMapping("/refreshToken")
    @Override
    public AuthResponse refreshToken(String refreshToken) {
        return refreshTokenService.refreshToken(refreshToken);
    }
}
