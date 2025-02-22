package com.example.getirme.controller;

import com.example.getirme.jwt.AuthRequest;
import com.example.getirme.jwt.AuthResponse;

public interface IUserController {
    AuthResponse login(AuthRequest request);
}
