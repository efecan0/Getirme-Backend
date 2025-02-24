package com.example.getirme.service.impl;

import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import com.example.getirme.exception.MessageType;
import com.example.getirme.jwt.AuthRequest;
import com.example.getirme.jwt.AuthResponse;
import com.example.getirme.model.User;
import com.example.getirme.repository.UserRepository;
import com.example.getirme.service.IRefreshTokenService;
import com.example.getirme.service.IUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.getirme.exception.MessageType.BAD_REQUEST;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Transactional
    @Override
    public AuthResponse login(AuthRequest request) throws AuthenticationException {
           String phoneNumber = request.getPhoneNumber().replaceAll(" " , "");
           UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber, request.getPassword());
           authenticationProvider.authenticate(authenticationToken);
           Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
           if(user.isPresent()) {
               return refreshTokenService.generateTokens( user.get() );
           }
           throw new BaseException(new ErrorMessage(BAD_REQUEST , "Invalid phone number or password."));
    }
}
