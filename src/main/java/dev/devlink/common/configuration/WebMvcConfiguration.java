package dev.devlink.common.configuration;

import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import dev.devlink.common.interceptor.JwtAuthInterceptor;
import dev.devlink.common.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final TokenProvider tokenProvider;
    private final AuthMemberIdArgumentResolver authResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor(tokenProvider))

                // 인증이 필요한 경로
                .addPathPatterns(
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
