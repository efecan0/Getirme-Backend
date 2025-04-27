package com.example.getirme.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String userId = (String) attributes.get("userId");
        if (userId == null) {
            System.out.println("❌ Handshake sırasında userId bulunamadı.");
            return null;
        }
        System.out.println("✅ Handshake sırasında userId bulundu: " + userId);
        return new StompPrincipal(userId);
    }
}
