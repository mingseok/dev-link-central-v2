package dev.devlink.common.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secretKey = "myVerySecretKeyThatIsAtLeast256BitsLongForHMACHS256Algorithm";
    private final Long testMemberId = 1L;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secretKey);
    }

    @Test
    @DisplayName("토큰 생성 시 JwtToken 객체를 반환한다")
    void generateToken() {
        // when
        JwtToken result = jwtTokenProvider.generateToken(testMemberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrantType()).isEqualTo("Bearer");
        assertThat(result.getAccessToken()).isNotBlank();
    }

    @Test
    @DisplayName("생성된 토큰에서 멤버 ID를 추출할 수 있다")
    void extractMemberId() {
        // given
        JwtToken token = jwtTokenProvider.generateToken(testMemberId);

        // when
        Long extractedMemberId = jwtTokenProvider.extractMemberId(token.getAccessToken());

        // then
        assertThat(extractedMemberId).isEqualTo(testMemberId);
    }

    @Test
    @DisplayName("유효한 토큰은 검증을 통과한다")
    void validateToken_ValidToken() {
        // given
        JwtToken token = jwtTokenProvider.generateToken(testMemberId);

        // when
        boolean isValid = jwtTokenProvider.validateToken(token.getAccessToken());

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰은 검증에 실패한다")
    void validateToken_InvalidToken() {
        // given
        String invalidToken = "invalid.token.format";

        // when
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 검증에 실패한다")
    void validateToken_ExpiredToken() {
        // given
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        String expiredToken = Jwts.builder()
                .setSubject(String.valueOf(testMemberId))
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 25)) // 25시간 전
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1시간 전 만료
                .signWith(key)
                .compact();

        // when
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("null 토큰은 검증에 실패한다")
    void validateToken_NullToken() {
        // when
        boolean isValid = jwtTokenProvider.validateToken(null);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("빈 문자열 토큰은 검증에 실패한다")
    void validateToken_EmptyToken() {
        // when
        boolean isValid = jwtTokenProvider.validateToken("");

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("잘못된 토큰에서 멤버 ID 추출 시 예외가 발생한다")
    void extractMemberId_InvalidToken() {
        // given
        String invalidToken = "invalid.token.format";

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.extractMemberId(invalidToken))
                .isInstanceOf(Exception.class);
    }
}
