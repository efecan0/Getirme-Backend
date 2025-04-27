package com.example.getirme.jwt;

import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.io.IOException;

import static com.example.getirme.exception.MessageType.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/login") || path.equals("/restaurant/register") ||
                path.equals("/customer/register") || path.equals("/refreshToken") ||
                path.startsWith("/ws/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {;
        String token = null;
        String phoneNumber;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token == null) {
            filterChain.doFilter(request, response);
            throw new BaseException(new ErrorMessage(UNAUTHORIZED , null));
        }

        try{
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


    public Authentication getAuthentication(String token) {
        String phoneNumber = jwtService.getPhoneNumberByToken(token);
        if (phoneNumber != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }
        return null;
    }
}
