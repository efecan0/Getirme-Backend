package com.example.getirme.service;

import com.example.getirme.jwt.AuthResponse;
import com.example.getirme.model.User;

public interface IRefreshTokenService {
    AuthResponse refreshToken(String refreshToken);
    String generateAndSaveRefreshToken(User user);
    AuthResponse generateTokens(User user);
}
