package com.example.targetrecruiting.common.jtw;

import com.example.targetrecruiting.common.security.SecurityExceptionDto;
import com.example.targetrecruiting.user.entity.User;
import com.example.targetrecruiting.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //JWT 해석하여 추출
        String accessToken = jwtUtil.resolveToken(request, JwtUtil.ACCESS_TOKEN);
        String refreshToken = jwtUtil.resolveToken(request, JwtUtil.REFRESH_TOKEN);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
        } else {
            if (jwtUtil.validateToken(accessToken)) {
                setAuthentication(jwtUtil.getUserInfoFromToken(accessToken));
            } else if (refreshToken != null && jwtUtil.refreshTokenValid(refreshToken)) {
                //Refresh 토큰으로 유저명 가져오기
                String userEmail = jwtUtil.getUserInfoFromToken(refreshToken);
                //유저명으로 유저 정보 가져오기
                User user = userRepository.findByEmail(userEmail).orElseThrow(
                        () -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                log.info("New Access Token");
                String newAccessToken = jwtUtil.createToken(user.getEmail(), "Access", user.getId());

                //Header AccessToken 추가
                jwtUtil.setHeaderAccessToken(response, newAccessToken);
                setAuthentication(userEmail);

                log.info("New Refresh Token");
                long refreshTime = jwtUtil.getExpirationTime(refreshToken);
                String newRefreshToken = jwtUtil.createNewRefreshToken(user.getEmail(), refreshTime, user.getId());

                //Header AccessToken 추가
                jwtUtil.setHeaderRefreshToken(response, newRefreshToken);
            } else if (refreshToken == null) {
                jwtExceptionHandler(response, "Expired AccessToken", HttpStatus.BAD_REQUEST.value());
                return;
            } else {
                jwtExceptionHandler(response, "Expired RefreshToken please login again", HttpStatus.BAD_REQUEST.value());
                return;
            }
            //다음 필터로 요청과 응답을 전달하여 필터 체인 계속 실행
            filterChain.doFilter(request, response);
        }
    }

    // 인증 객체를 생성하여 SecurityContext 에 설정
    public void setAuthentication(String userEmail) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(userEmail);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // JWT 예외 처리를 위한 응답 설정
    public void jwtExceptionHandler(HttpServletResponse response, String message, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json; charset=utf8"); //한국어 깨짐 문제 해결
        try {
            // 예외 정보를 JSON 형태로 변환하여 응답에 작성
            String json = new ObjectMapper().writeValueAsString(new SecurityExceptionDto(statusCode, message));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
