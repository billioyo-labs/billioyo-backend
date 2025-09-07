package com.itemrental.rentalService.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final SecretKey key;
    private final RedisUtil redisUtil;

    @Value("${ACCESSTOKEN_VALIDTIME}")
    private Long accessTokenValidTime;

    @Value("${REFRESHTOKEN_VALIDTIME}")
    private Long refreshTokenValidTime;

    @Autowired
    public JwtTokenProvider(@Value("${TOKEN_SECRET}")String secretKey, RedisUtil redisUtil){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisUtil = redisUtil;
    }

    public String createJwt(String category, String username, Long expiredMs) {
        Date now = new Date();
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiredMs))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | DecodingException e) {
            log.info("🔐 Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("⌛ Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("❌ Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("⚠️ JWT claims string is empty.", e);
        }
        return false;
    }

    public Claims parseClaims(String accessToken){
        try{
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken).getPayload();
        }catch (ExpiredJwtException e){
            return e.getClaims();

        }
    }

    public String getUserName(String token){
        return parseClaims(token).get("username",String.class);
    }
    public String getCategory(String token){
        return parseClaims(token).get("category",String.class);
    }
}
