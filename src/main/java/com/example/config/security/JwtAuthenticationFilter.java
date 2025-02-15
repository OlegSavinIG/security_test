package com.example.config.security;

import com.example.service.OurUserDetailedService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTUtils jwtUtils;

    private final OurUserDetailedService ourUserDetailedService;

    // Метод, выполняемый для каждого HTTP запроса
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Шаг 1: Извлечение заголовка авторизации из запроса
        String authorizationHeader = request.getHeader("Authorization");

        // Шаг 2: Проверка наличия заголовка авторизации
        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }
        // Шаг 3: Извлечение токена из заголовка
        String jwtToken = authorizationHeader.substring(7);
        // Шаг 4: Извлечение имени пользователя из JWT токена
        String username = jwtUtils.extractUsername(jwtToken);
        // Шаг 5: Проверка валидности токена и аутентификации
        if (username != null) {
            UserDetails userDetails = ourUserDetailedService.loadUserByUsername(username);
            if (jwtUtils.isTokenValid(jwtToken, userDetails)){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            };
        }

        // Шаг 7: Передача запроса на дальнейшую обработку в фильтрующий цепочке
        filterChain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/register");
    }
}

