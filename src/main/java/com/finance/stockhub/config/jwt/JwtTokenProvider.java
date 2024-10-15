package com.finance.stockhub.config.jwt;

import com.finance.stockhub.config.auth.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${jwt.secret-key.access}")
    private String ACCESS_SECRET_KEY;

    @Value("${jwt.secret-key.refresh}")
    private String REFRESH_SECRET_KEY;

    @Value("${jwt.access-token.expiration}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token.expiration}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    private Key getKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(CustomUserDetails userDetails, JwtTokenType tokenType) {
        Claims claims = Jwts.claims();
        claims.put("email", userDetails.getUsername());

        String secretKey = ACCESS_SECRET_KEY;
        long expiredTime = ACCESS_TOKEN_EXPIRE_TIME;

        if (tokenType.equals(JwtTokenType.REFRESH)) {
            secretKey = REFRESH_SECRET_KEY;
            expiredTime = REFRESH_TOKEN_EXPIRE_TIME;
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(getKey(secretKey), SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims parseClaims(String token, JwtTokenType tokenType) {
        String secretKey = ACCESS_SECRET_KEY;

        if (tokenType == JwtTokenType.REFRESH) {
            secretKey = REFRESH_SECRET_KEY;
        }

        return Jwts.parserBuilder()
                .setSigningKey(getKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmail(String token, JwtTokenType tokenType) {
        return parseClaims(token, tokenType).get("email", String.class);
    }

    public boolean validateToken(String token, JwtTokenType tokenType) {
        try {
            parseClaims(token, tokenType);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다");
        } catch (ExpiredJwtException e) {
            log.error("잘못된 JWT Token입니다");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT Token입니다");
        } catch (IllegalArgumentException e) {
            log.error("JWT Token이 잘못되었습니다");
        }

        return false;
    }

}
