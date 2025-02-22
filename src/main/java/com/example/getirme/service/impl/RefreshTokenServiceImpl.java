package com.example.getirme.service.impl;

import com.example.getirme.jwt.AuthResponse;
import com.example.getirme.jwt.JwtService;
import com.example.getirme.model.RefreshToken;
import com.example.getirme.model.User;
import com.example.getirme.repository.RefreshTokenRepository;
import com.example.getirme.service.IRefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        RefreshToken dbRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new RuntimeException("Refresh token not found"));
        if(dbRefreshToken.getExpireDate().before(new Date())){
            return generateTokens(dbRefreshToken.getUser());
        }
        throw new RuntimeException("Refresh token expired");
    }

    public String generateAndSaveRefreshToken(User user){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpireDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getRefreshToken();
    }

    public AuthResponse generateTokens(User user){
        String generatedAccessToken = jwtService.generateToken(user.getUsername());
        String generatedRefreshToken = generateAndSaveRefreshToken(user);
        return new AuthResponse(generatedAccessToken, generatedRefreshToken);
    }

}
