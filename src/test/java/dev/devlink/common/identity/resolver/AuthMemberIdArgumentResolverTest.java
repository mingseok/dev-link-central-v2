package dev.devlink.common.identity.resolver;

import dev.devlink.common.identity.annotation.AuthMemberId;
import dev.devlink.common.identity.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthMemberIdArgumentResolverTest {

    private AuthMemberIdArgumentResolver resolver;
    private MethodParameter methodParameter;
    private NativeWebRequest webRequest;
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        resolver = new AuthMemberIdArgumentResolver();
        methodParameter = mock(MethodParameter.class);
        webRequest = mock(NativeWebRequest.class);
        httpServletRequest = mock(HttpServletRequest.class);
    }

    @Test
    @DisplayName("AuthMemberId 어노테이션과 Long 타입 파라미터를 지원한다")
    void supportsParameter_ValidParameter() {
        // given
        when(methodParameter.hasParameterAnnotation(AuthMemberId.class)).thenReturn(true);
        when(methodParameter.getParameterType()).thenReturn((Class) Long.class);

        // when
        boolean result = resolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("AuthMemberId 어노테이션이 없으면 지원하지 않는다")
    void supportsParameter_NoAnnotation() {
        // given
        when(methodParameter.hasParameterAnnotation(AuthMemberId.class)).thenReturn(false);
        when(methodParameter.getParameterType()).thenReturn((Class) Long.class);

        // when
        boolean result = resolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Long 타입이 아니면 지원하지 않는다")
    void supportsParameter_NotLongType() {
        // given
        when(methodParameter.hasParameterAnnotation(AuthMemberId.class)).thenReturn(true);
        when(methodParameter.getParameterType()).thenReturn((Class) String.class);

        // when
        boolean result = resolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("요청 속성에서 memberId를 올바르게 추출한다")
    void resolveArgument_ValidMemberId() {
        // given
        Long expectedMemberId = 123L;
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getAttribute("memberId")).thenReturn(expectedMemberId);

        // when
        Object result = resolver.resolveArgument(methodParameter, null, webRequest, null);

        // then
        assertThat(result).isEqualTo(expectedMemberId);
    }

    @Test
    @DisplayName("memberId가 Long 타입이 아니면 UnauthorizedException을 던진다")
    void resolveArgument_InvalidMemberIdType() {
        // given
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getAttribute("memberId")).thenReturn("invalidType");

        // when & then
        assertThatThrownBy(() -> resolver.resolveArgument(methodParameter, null, webRequest, null))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("memberId가 null이면 UnauthorizedException을 던진다")
    void resolveArgument_NullMemberId() {
        // given
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getAttribute("memberId")).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> resolver.resolveArgument(methodParameter, null, webRequest, null))
                .isInstanceOf(UnauthorizedException.class);
    }
}
