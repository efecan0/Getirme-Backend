package com.example.getirme.jwt;

import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import com.example.getirme.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.example.getirme.exception.MessageType.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header;
        String token;
        String phoneNumber;

        header = request.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            throw new BaseException(new ErrorMessage(UNAUTHORIZED , null));
        }

        try{

            token = header.substring(7);
            phoneNumber = jwtService.getPhoneNumberByToken(token);

            if(token != null && phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null && !jwtService.isTokenExpired(token)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(phoneNumber, null, userDetails.getAuthorities());
                authentication.setDetails(userDetails);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }
        catch(ExpiredJwtException e){
            throw new BaseException(new ErrorMessage(UNAUTHORIZED , null));
        }
        catch (Exception e){
            throw new BaseException(new ErrorMessage(GENERAL_ERROR , null));
        }
        filterChain.doFilter(request, response);
    }
}
