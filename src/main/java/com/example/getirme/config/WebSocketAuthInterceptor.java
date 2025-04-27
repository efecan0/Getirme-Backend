package com.example.getirme.config;

import com.example.getirme.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String accessToken = null;

            if (httpRequest.getCookies() != null) {
                for (Cookie cookie : httpRequest.getCookies()) {
                    if ("accessToken".equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (accessToken != null) {
                Authentication authentication = jwtAuthenticationFilter.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (authentication != null && authentication.getPrincipal() instanceof com.example.getirme.model.User user) {
                    attributes.put("userId", String.valueOf(user.getId()));
                    System.out.println("✅ WebSocket Authentication başarılı! User ID: " + user.getId());
                    return true;
                }
            }
        }
        System.out.println("❌ WebSocket Authentication başarısız!");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
}
