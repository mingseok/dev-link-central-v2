package dev.devlink.common.identity.exception;

import dev.devlink.common.exception.ErrorCode;
import dev.devlink.common.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class UnauthorizedExceptionTest {

    @Test
    @DisplayName("UnauthorizedException은 ServiceException을 상속한다")
    void extendsServiceException() {
        // given
        UnauthorizedException exception = new UnauthorizedException();

        // then
        assertThat(exception).isInstanceOf(ServiceException.class);
    }

    @Test
    @DisplayName("UNAUTHORIZED 에러 코드를 가진다")
    void hasUnauthorizedErrorCode() {
        // given
        UnauthorizedException exception = new UnauthorizedException();

        // then
        assertThat(exception.getCommonError()).isEqualTo(ErrorCode.UNAUTHORIZED);
        assertThat(exception.getCommonError().getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getCommonError().getCode()).isEqualTo("401");
        assertThat(exception.getCommonError().getMessage()).isEqualTo("인증이 필요합니다.");
    }

    @Test
    @DisplayName("기본 생성자로 생성할 수 있다")
    void canCreateWithDefaultConstructor() {
        // when & then
        UnauthorizedException exception = new UnauthorizedException();
        
        assertThat(exception).isNotNull();
    }
}
