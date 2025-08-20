package dev.devlink.member.service.dto.response;

import dev.devlink.common.jwt.JwtToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenResponseTest {

    @Test
    @DisplayName("생성자로 JWT 토큰 응답을 생성할 수 있다")
    void constructor_CreatesJwtTokenResponse() {
        // given
        String accessToken = "accessToken123";
        String refreshToken = "refreshToken456";

        // when
        JwtTokenResponse response = new JwtTokenResponse(accessToken, refreshToken);

        // then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("JwtToken 객체로부터 응답을 생성할 수 있다")
    void from_CreatesJwtTokenResponseFromJwtToken() {
        // given
        JwtToken jwtToken = JwtToken.builder()
                .accessToken("accessToken123")
                .refreshToken("refreshToken456")
                .build();

        // when
        JwtTokenResponse response = JwtTokenResponse.from(jwtToken);

        // then
        assertThat(response.getAccessToken()).isEqualTo("accessToken123");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken456");
    }

    @Test
    @DisplayName("getter가 올바르게 동작한다")
    void getters_WorkCorrectly() {
        // given
        JwtTokenResponse response = new JwtTokenResponse(
                "testAccessToken",
                "testRefreshToken"
        );

        // when & then
        assertThat(response.getAccessToken()).isEqualTo("testAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("testRefreshToken");
    }
}
