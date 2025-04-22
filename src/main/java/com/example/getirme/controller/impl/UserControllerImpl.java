package com.example.getirme.controller.impl;

import com.example.getirme.controller.IUserController;
import com.example.getirme.jwt.AuthRequest;
import com.example.getirme.jwt.AuthResponse;
import com.example.getirme.model.RootEntity;
import com.example.getirme.model.User;
import com.example.getirme.repository.UserRepository;
import com.example.getirme.service.IUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@RestController
public class UserControllerImpl extends BaseController implements IUserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    @Override
    public ResponseEntity<RootEntity<String>> login(@Valid @RequestBody AuthRequest request,  HttpServletResponse response) {
        AuthResponse authResponse = userService.login(request);
        Cookie accessTokenCookie = new Cookie("accessToken", authResponse.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge((int) Duration.ofHours(4).getSeconds());

        Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(false);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) Duration.ofHours(8).getSeconds());

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ok("Successfully Logined.");
    }

}
