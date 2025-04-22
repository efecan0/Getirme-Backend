package com.example.getirme.controller;

import com.example.getirme.jwt.AuthRequest;
import com.example.getirme.jwt.AuthResponse;
import com.example.getirme.model.RootEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface IUserController {
    ResponseEntity<RootEntity<String>> login(AuthRequest request ,  HttpServletResponse response);
}
