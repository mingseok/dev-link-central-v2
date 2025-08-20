package dev.devlink.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceExceptionTest {

    @Test
    @DisplayName("서비스 예외를 생성할 수 있다")
    void create_Success() {
        // given
        TestError testError = new TestError();

        // when
        ServiceException exception = new ServiceException(testError);

        // then
        assertThat(exception.getMessage()).isEqualTo("테스트 에러입니다");
        assertThat(exception.getCommonError()).isEqualTo(testError);
    }

    @Test
    @DisplayName("예외가 RuntimeException을 상속한다")
    void extendsRuntimeException() {
        // given
        TestError testError = new TestError();

        // when
        ServiceException exception = new ServiceException(testError);

        // then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("CommonError를 반환한다")
    void getCommonError_ReturnsCorrectError() {
        // given
        TestError testError = new TestError();
        ServiceException exception = new ServiceException(testError);

        // when
        CommonError returnedError = exception.getCommonError();

        // then
        assertThat(returnedError).isEqualTo(testError);
        assertThat(returnedError.getCode()).isEqualTo("TEST_ERROR");
        assertThat(returnedError.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // 테스트용 CommonError 구현체
    private static class TestError implements CommonError {
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
            return "테스트 에러입니다";
        }
    }
}
