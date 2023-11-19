package com.example.targetrecruiting.common.config;

import com.example.targetrecruiting.common.jtw.JwtUtil;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Carrot Clone")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI carrotCloneAPI() {
        Info info = new Info().title("test swagger")
                .description("swagger test")
                .version("v1.0.0")
                .license(new License().name("Apache 2.0").url("http://springdoc.org"));

        String access_token_header = JwtUtil.ACCESS_TOKEN;
        String refresh_token_header = JwtUtil.REFRESH_TOKEN;

        // 헤더에 security scheme 도 같이 보내게 만드는 것
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(access_token_header).addList(refresh_token_header);

        Components components = new Components()
                .addSecuritySchemes(access_token_header,
                        new SecurityScheme()
                                .name(access_token_header)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                .addSecuritySchemes(refresh_token_header,
                        new SecurityScheme()
                                .name(refresh_token_header)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .scheme("bearer")
                                .bearerFormat("JWT"));

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }

}
