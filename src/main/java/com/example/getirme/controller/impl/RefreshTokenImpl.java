package com.example.getirme.controller.impl;

import com.example.getirme.controller.IRefreshTokenController;
import com.example.getirme.jwt.AuthResponse;
import com.example.getirme.model.RootEntity;
import com.example.getirme.service.IRefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefreshTokenImpl extends BaseController implements IRefreshTokenController {

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @PostMapping("/refreshToken")
    @Override
    public ResponseEntity<RootEntity<AuthResponse>> refreshToken(String refreshToken) {
        AuthResponse response = refreshTokenService.refreshToken(refreshToken);
        return ok(response);
    }
}
