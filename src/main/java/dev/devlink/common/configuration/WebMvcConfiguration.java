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

    public static final String CLOSED_API_PREFIX = "/api/v1/**";
    public static final String PUBLIC_API_PREFIX = "/api/public/**";
    public static final String VIEW_PAGE_PREFIX = "/view/**";

    private final AuthMemberIdArgumentResolver authResolver;
    private final TokenProvider tokenProvider;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor(tokenProvider))
                .addPathPatterns(CLOSED_API_PREFIX)
                .excludePathPatterns(
                        PUBLIC_API_PREFIX,
                        VIEW_PAGE_PREFIX,
                        "/favicon.ico",
                        "/static/**",
                        "/css/**",
                        "/images/**"
                );
    }
}
