package com.example.getirme.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

   protected final String SECRET_KEY = "lY2ETUTYNA5bepp5S0dyma8CVBLrBJR8l1sroR4htzo=";

   public String generateToken(String phoneNumber) {
       return Jwts.builder()
               .setSubject(phoneNumber)
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 4))
               .signWith(getSigningKey(), SignatureAlgorithm.HS256)
               .compact();
   }

   public Key getSigningKey() {
       byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
       return Keys.hmacShaKeyFor(keyBytes);
   }

   public Claims getClaims(String token) {
       return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
   }

   public Object getClaimsByKey(String token, String key) {
       Claims claims = getClaims(token);
       return claims.get(key);
   }

   public <T> T getTokenVariable(String token , Function<Claims , T> claimsTFunction){
       Claims claims = getClaims(token);
       return claimsTFunction.apply(claims);
   }

   public boolean isTokenExpired(String token) {
       return new Date().after(getTokenVariable(token , Claims::getExpiration));
   }

   public String getPhoneNumberByToken(String token) {
       return getTokenVariable(token , Claims::getSubject);
   }

}
