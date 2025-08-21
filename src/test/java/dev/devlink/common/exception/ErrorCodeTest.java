package dev.devlink.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @Test
    @DisplayName("BAD_REQUEST 에러 코드 정보가 올바르다")
    void badRequestErrorCode() {
        // given
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;

        // then
        assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorCode.getCode()).isEqualTo("400");
        assertThat(errorCode.getMessage()).isEqualTo("잘못된 요청입니다.");
    }

    @Test
    @DisplayName("INTERNAL_SERVER_ERROR 에러 코드 정보가 올바르다")
    void internalServerErrorCode() {
        // given
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        // then
        assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(errorCode.getCode()).isEqualTo("500");
        assertThat(errorCode.getMessage()).isEqualTo("내부 서버 오류가 발생했습니다. 관리자에게 문의해주세요.");
    }

    @Test
    @DisplayName("METHOD_NOT_ALLOWED 에러 코드 정보가 올바르다")
    void methodNotAllowedErrorCode() {
        // given
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

        // then
        assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(errorCode.getCode()).isEqualTo("405");
        assertThat(errorCode.getMessage()).isEqualTo("지원하지 않는 Http Method 입니다.");
    }

    @Test
    @DisplayName("INVALID_PARAMETER_TYPE 에러 코드 정보가 올바르다")
    void invalidParameterTypeErrorCode() {
        // given
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER_TYPE;

        // then
        assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorCode.getCode()).isEqualTo("400");
        assertThat(errorCode.getMessage()).isEqualTo("잘못된 파라미터 타입입니다.");
    }

    @Test
    @DisplayName("UNAUTHORIZED 에러 코드 정보가 올바르다")
    void unauthorizedErrorCode() {
        // given
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        // then
        assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(errorCode.getCode()).isEqualTo("401");
        assertThat(errorCode.getMessage()).isEqualTo("인증이 필요합니다.");
    }

    @Test
    @DisplayName("모든 ErrorCode는 CommonError 인터페이스를 구현한다")
    void allErrorCodesImplementCommonError() {
        // given
        ErrorCode[] errorCodes = ErrorCode.values();

        // then
        for (ErrorCode errorCode : errorCodes) {
            assertThat(errorCode).isInstanceOf(CommonError.class);
            assertThat(errorCode.getHttpStatus()).isNotNull();
            assertThat(errorCode.getCode()).isNotNull();
            assertThat(errorCode.getMessage()).isNotNull();
        }
    }

    @Test
    @DisplayName("각 ErrorCode의 HTTP 상태 코드와 코드 값이 일치한다")
    void httpStatusAndCodeConsistency() {
        // then
        assertThat(ErrorCode.BAD_REQUEST.getHttpStatus().value())
                .isEqualTo(Integer.parseInt(ErrorCode.BAD_REQUEST.getCode()));
        assertThat(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value())
                .isEqualTo(Integer.parseInt(ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
        assertThat(ErrorCode.METHOD_NOT_ALLOWED.getHttpStatus().value())
                .isEqualTo(Integer.parseInt(ErrorCode.METHOD_NOT_ALLOWED.getCode()));
        assertThat(ErrorCode.UNAUTHORIZED.getHttpStatus().value())
                .isEqualTo(Integer.parseInt(ErrorCode.UNAUTHORIZED.getCode()));
    }
}
