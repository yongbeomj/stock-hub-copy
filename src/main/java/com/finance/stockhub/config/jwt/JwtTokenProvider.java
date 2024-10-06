package com.finance.stockhub.config.jwt;

import com.finance.stockhub.config.auth.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_PRIFIX = "Bearer ";
    @Value("${jwt.access-token.expiration}")
    private long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${jwt.refresh-token.expiration}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String create(CustomUserDetails userDetails) {
        Claims claims = Jwts.claims();
        claims.put("email", userDetails.getUsername());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
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
