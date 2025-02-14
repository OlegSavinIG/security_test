package com.example.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
//@RequiredArgsConstructor
//@AllArgsConstructor
public class JWTUtils {

    // Ключ шифрования для JWT
//    @Autowired
    private final SecretKey secretKey;

    // Время действия токена в миллисекундах (24 часа)
    private static final long EXPIRATION_TIME = 86400000;
    private static final long REFRESHED_EXPIRATION_TIME = 604800000;

    public JWTUtils(){
        // Строка, используемая для создания секретного ключа
        String secreteString = "MySuperSecretKeyForJWT1234567891234567";//
        byte[] keyBytes = secreteString.getBytes(StandardCharsets.UTF_8);
        secretKey = Keys.hmacShaKeyFor(keyBytes);//
    }
    /*Метод для генерации JWT токена на основе данных пользователя*/
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .issuer("Oleg")
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    // Метод для генерации токена обновления (refresh token) с дополнительными данными
    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails){
        return Jwts.builder()
                .claims(claims)
                .issuer("Oleg")
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+REFRESHED_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    // Метод для извлечения имени пользователя из токена
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(
                Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
        );
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        String username2 = userDetails.getUsername();
        return username.equals(username2) && !isTokenExpired(token);
    }


        private boolean isTokenExpired(String token) {
            return extractClaims(token, Claims::getExpiration).before(new Date());
        }
    }

