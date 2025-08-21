package dev.devlink.common.exception;

import jakarta.validation.Payload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class CommonErrorTest {

    @Test
    @DisplayName("CommonError는 Payload를 상속한다")
    void extendsPayload() {
        // then
        assertThat(Payload.class).isAssignableFrom(CommonError.class);
    }

    @Test
    @DisplayName("CommonError 구현체는 필수 메서드를 제공한다")
    void implementsRequiredMethods() {
        // given
        CommonError testError = new TestCommonError();

        // then
        assertThat(testError.getHttpStatus()).isNotNull();
        assertThat(testError.getCode()).isNotNull();
        assertThat(testError.getMessage()).isNotNull();
    }

    // 테스트용 CommonError 구현체
    private static class TestCommonError implements CommonError {
        @Override
        public HttpStatus getHttpStatus() {
            return HttpStatus.BAD_REQUEST;
        }

        @Override
        public String getCode() {
            return "TEST_ERROR";
        }

        @Override
        public String getMessage() {
            return "테스트 에러 메시지";
        }
    }
}
