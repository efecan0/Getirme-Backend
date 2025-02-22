package com.example.getirme.service;

import com.example.getirme.jwt.AuthRequest;
import com.example.getirme.jwt.AuthResponse;

public interface IUserService {
    AuthResponse login(AuthRequest request);
}
