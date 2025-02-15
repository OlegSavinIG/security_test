package com.example.config.security;

import com.example.model.AuthRequest;
import com.example.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginAttemptFilter extends OncePerRequestFilter {
    private final LoginService loginService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod())
                && "/login".equals(request.getServletPath())) {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            filterChain.doFilter(wrappedRequest, response);

            if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
                String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
                AuthRequest authRequest = objectMapper.readValue(requestBody, AuthRequest.class);
                String username = authRequest.getUsername();

                log.warn("Failed login attempt for user: {}", username);
                loginService.increaseFailsCounter(username);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
