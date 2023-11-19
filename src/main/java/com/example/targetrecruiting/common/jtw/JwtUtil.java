package com.example.targetrecruiting.common.jtw;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private static final long ACCESS_TIME = Duration.ofMinutes(60).toMillis();
    private static final long REFRESH_TIME = Duration.ofDays(30).toMillis();

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    //jwt secretKey 검증
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기
    public String resolveToken(HttpServletRequest request, String token) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 생성
    public JwtTokenDto createAllToken(String email, Long id){
        return new JwtTokenDto(createToken(email,"Access",id),createToken(email,"Refresh",id));
    }

    public String createToken(String email,String token, Long id){
        Date date = new Date();
        long time = token.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim("userId", id)
                        .setExpiration(new Date(date.getTime()+time))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    //토큰검증
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (SecurityException | MalformedJwtException e){
            log.info("Invalid JWT signature");
        }catch (ExpiredJwtException e){
            log.info("Expired JWT token");
        }catch (UnsupportedJwtException e){
            log.info("Unsupported JWT token");
        }catch (IllegalArgumentException e){
            log.info("JWT claims is empty");
        }
        return false;
    }
    //토큰에서 사용자 정보 가져오기
    public String getUserInformToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }


}