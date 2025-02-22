package com.example.getirme.controller.impl;

import com.example.getirme.controller.IUserController;
import com.example.getirme.jwt.AuthRequest;
import com.example.getirme.jwt.AuthResponse;
import com.example.getirme.model.User;
import com.example.getirme.repository.UserRepository;
import com.example.getirme.service.IUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserControllerImpl implements IUserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    @Override
    public AuthResponse login(@RequestBody AuthRequest request) {
        return userService.login(request);
    }

    @Transactional
    @GetMapping("/getUser/{id}")
    public User getUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        return user;
    }
}
