package com.friendbook.utility;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestedRoute = request.getRequestURI();

        // 1. FIXED: Only skip the public authentication endpoints
        // If you skip everything starting with "/auth", your profile update will fail!
        if (requestedRoute.equals("/auth/login") || requestedRoute.equals("/auth/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;

        // 2. Check Header (For AJAX updateProfile calls)
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 3. Check Cookie (For the /profile/{username} page redirect)
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        // 4. Validation Logic
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                jwtUtil.validateToken(token);
                String username = jwtUtil.extractClaims(token).getSubject();

                if (username != null) {
                    // In a monolith, we use the username (email) from the token
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, new ArrayList<>());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // DEBUG: Verify it found the user
                    System.out.println("Authenticated user: " + username);
                }
            } catch (Exception e) {
                System.out.println("JWT Validation Error: " + e.getMessage());
                // Clear invalid cookie
                Cookie cookie = new Cookie("jwtToken", null);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        filterChain.doFilter(request, response);
    }
}