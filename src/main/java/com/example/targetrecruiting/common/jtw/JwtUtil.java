package com.example.targetrecruiting.common.jtw;

import com.example.targetrecruiting.common.jtw.refreshToken.RefreshToken;
import com.example.targetrecruiting.common.jtw.refreshToken.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN = "ACCESSTOKEN";
    public static final String REFRESH_TOKEN = "REFRESHTOKEN";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final long ACCESS_TIME = Duration.ofMinutes(60).toMillis();
    private static final long REFRESH_TIME = Duration.ofDays(30).toMillis();

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

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

    //JWT 검증
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

    //Refresh 토큰 재발급
    public String createNewRefreshToken(String email, long time, Long id){
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim("userId", id)
                        .setExpiration(new Date(date.getTime()+time))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    //토큰에서 사용자 정보 가져오기
    public String getUserInformToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    //인증객체 생성
    public Authentication createAuthentication(String email){
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }

    //RefreshToken 검증
    public boolean refreshTokenValid(String token){
        if (!validateToken(token)) return false;
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(getUserInformToken(token));

        //해당유저의 리프레시 토큰이 DB에 없을 경우 예외처리
        if (refreshToken.isEmpty()){
            throw new IllegalArgumentException("Expired JWT");
        }
        return token.equals(refreshToken.get().getRefreshToken().substring(7));
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken){
        response.setHeader(ACCESS_TOKEN,accessToken);
    }

    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken){
        response.setHeader(REFRESH_TOKEN,refreshToken);
    }

    public void createAndSetToken(HttpServletResponse response, String email, Long id){
        JwtTokenDto jwtTokenDto = createAllToken(email,id);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(email);
        if (refreshToken.isPresent()){
            refreshTokenRepository.save(refreshToken.get().updateToken(jwtTokenDto.getRefreshToken()));
        }else {
            RefreshToken newRefreshToken = new RefreshToken(jwtTokenDto.getRefreshToken(), email);
            refreshTokenRepository.save(newRefreshToken);
        }
        response.setHeader(JwtUtil.ACCESS_TOKEN, jwtTokenDto.getAccessToken());
        response.setHeader(JwtUtil.REFRESH_TOKEN, jwtTokenDto.getRefreshToken());
    }

    //토큰에서 만료시간 정보 추출
    public long getExpiredTime(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        //현재시간과 만료 시간의 차이를 계산하여 반환
        Date expirationDate = claims.getExpiration();
        Date now = new Date();
        return (expirationDate.getTime() - now.getTime()) / 1000;
    }
}