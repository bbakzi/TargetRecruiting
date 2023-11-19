package com.example.targetrecruiting.common.config;

import com.example.targetrecruiting.common.jtw.JwtAuthFilter;
import com.example.targetrecruiting.common.jtw.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    private static final String[] PERMIT_URL_ARRAY = {
            "/api/users/login",
            "/api/users/signup",
//            "/api/users/kakao"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // resources 접근 허용 설정
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console())  // H2 > MySQL 전환시 삭제
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(PERMIT_URL_ARRAY).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/email-check").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/projects/{project-id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/portfolios").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/portfolios/{portfolio-id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/portfolios/id").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/portfolios/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/portfolios/autocomplete").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/statistics").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/statistics/develop").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/statistics/design").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/statistics/photographer").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/portfolios/popularity").permitAll()
                //swagger
                .requestMatchers("/swagger*/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 사전에 약속된 출처를 명시
//        config.addAllowedOrigin("https://ppol.pro");
        config.addAllowedOrigin("http://localhost:3000");

        // 특정 헤더를 클라이언트 측에서 사용할 수 있게 지정
        // 만약 지정하지 않는다면, Authorization 헤더 내의 토큰 값을 사용할 수 없음
        config.addExposedHeader(JwtUtil.AUTHORIZATION_HEADER);
        config.addExposedHeader(JwtUtil.ACCESS_TOKEN);
        config.addExposedHeader(JwtUtil.REFRESH_TOKEN);

        // 본 요청에 허용할 HTTP method(예비 요청에 대한 응답 헤더에 추가됨)
        config.addAllowedMethod("*");

        // 본 요청에 허용할 HTTP header(예비 요청에 대한 응답 헤더에 추가됨)
        config.addAllowedHeader("*");

        // 기본적으로 브라우저에서 인증 관련 정보들을 요청 헤더에 담지 않음
        // 이 설정을 통해서 브라우저에서 인증 관련 정보들을 요청 헤더에 담을 수 있도록 해줍니다.
        config.setAllowCredentials(true);

        // allowCredentials 를 true로 하였을 때,
        // allowedOrigin의 값이 * (즉, 모두 허용)이 설정될 수 없도록 검증합니다.
        config.validateAllowCredentials();

        // 어떤 경로에 이 설정을 적용할 지 명시합니다. (여기서는 전체 경로)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}