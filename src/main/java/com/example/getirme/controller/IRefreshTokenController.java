package com.example.getirme.controller;

import com.example.getirme.jwt.AuthResponse;
import com.example.getirme.model.RootEntity;
import org.springframework.http.ResponseEntity;

public interface IRefreshTokenController {
    ResponseEntity<RootEntity<AuthResponse>> refreshToken(String refreshToken);
}
