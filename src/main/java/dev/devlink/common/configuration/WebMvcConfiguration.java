package dev.devlink.common.configuration;

import dev.devlink.common.interceptor.JwtAuthInterceptor;
import dev.devlink.common.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final TokenProvider tokenProvider;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor(tokenProvider))

                // 인증이 필요한 경로
                .addPathPatterns(
                        "/api/v1/view/articles/save",
                        "/api/v1/articles/**",
                        "/api/v1/members/**"
                )

                // 인증이 필요한 없는 경로
                .excludePathPatterns(
                        "/api/v1/public/articles/**",
                        "/api/v1/public/members/**",
                        "/favicon.ico",
                        "/static/**",
                        "/css/**",
                        "/images/**"
                );
    }
}
