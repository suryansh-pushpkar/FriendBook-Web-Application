package com.friendbook.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import java.util.Date;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private final String SECRETKEY = "fadklfdrerieriovnmvncmmbberhkhgfkhkfhghdfghdfreriogflhjfk";

    public String generateToken(UserDetails user) {
        String token = Jwts.builder()
                .setClaims(Map.of("role",user.getAuthorities()))
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+60*60*1000))
                .signWith(Keys.hmacShaKeyFor(SECRETKEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        return token;
    }
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRETKEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public void validateToken(String token) {
        extractClaims(token);
    }
}