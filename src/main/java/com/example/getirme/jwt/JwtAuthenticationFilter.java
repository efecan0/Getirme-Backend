package com.example.getirme.jwt;

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
            return;
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
            throw new RuntimeException("EXPIRED JWT TOKEN");
        }
        catch (Exception e){
            throw new RuntimeException("GENERAL JWT ERROR");
        }
        filterChain.doFilter(request, response);
    }
}
