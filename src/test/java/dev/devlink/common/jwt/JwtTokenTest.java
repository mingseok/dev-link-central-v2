package dev.devlink.common.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenTest {

    @Test
    @DisplayName("JWT 토큰을 생성할 수 있다")
    void create_Success() {
        // when
        JwtToken token = JwtToken.builder()
                .grantType("Bearer")
                .accessToken("accessToken123")
                .refreshToken("refreshToken456")
                .build();

        // then
        assertThat(token.getGrantType()).isEqualTo("Bearer");
        assertThat(token.getAccessToken()).isEqualTo("accessToken123");
        assertThat(token.getRefreshToken()).isEqualTo("refreshToken456");
    }

    @Test
    @DisplayName("모든 인자 생성자로 JWT 토큰을 생성할 수 있다")
    void constructor_Success() {
        // when
        JwtToken token = new JwtToken(
                "Bearer",
                "accessToken789",
                "refreshToken012"
        );

        // then
        assertThat(token.getGrantType()).isEqualTo("Bearer");
        assertThat(token.getAccessToken()).isEqualTo("accessToken789");
        assertThat(token.getRefreshToken()).isEqualTo("refreshToken012");
    }

    @Test
    @DisplayName("일부 필드가 null이어도 JWT 토큰을 생성할 수 있다")
    void create_WithNullFields_Success() {
        // when
        JwtToken token = JwtToken.builder()
                .grantType("Bearer")
                .accessToken("accessToken123")
                .refreshToken(null)
                .build();

        // then
        assertThat(token.getGrantType()).isEqualTo("Bearer");
        assertThat(token.getAccessToken()).isEqualTo("accessToken123");
        assertThat(token.getRefreshToken()).isNull();
    }
}
