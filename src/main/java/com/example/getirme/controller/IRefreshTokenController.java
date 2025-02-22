package com.example.getirme.controller;

import com.example.getirme.jwt.AuthResponse;

public interface IRefreshTokenController {
    AuthResponse refreshToken(String refreshToken);
}
