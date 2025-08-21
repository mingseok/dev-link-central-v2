package dev.devlink.common.exception;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("ServiceException 처리 시 올바른 응답을 반환한다")
    void handleServiceException() {
        // given
        ServiceException ex = new ServiceException(ErrorCode.BAD_REQUEST);

        // when
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleServiceException(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(ApiResponse.Status.FAIL);
        assertThat(response.getBody().getMessage()).isEqualTo("잘못된 요청입니다.");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 처리 시 올바른 응답을 반환한다")
    void handleValidationException() {
        // given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "필드 검증 실패");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        // when
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleValidationException(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(ApiResponse.Status.FAIL);
        assertThat(response.getBody().getMessage()).isEqualTo("필드 검증 실패");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException에서 FieldError가 null인 경우 기본 메시지를 반환한다")
    void handleValidationException_NoFieldError() {
        // given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(null);

        // when
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleValidationException(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(ApiResponse.Status.FAIL);
        assertThat(response.getBody().getMessage()).isEqualTo("잘못된 요청입니다.");
    }

    @Test
    @DisplayName("DataIntegrityViolationException 처리 시 올바른 응답을 반환한다")
    void handleDataViolation() {
        // given
        DataIntegrityViolationException ex = new DataIntegrityViolationException("데이터 무결성 위반");

        // when
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleDataViolation(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(ApiResponse.Status.FAIL);
        assertThat(response.getBody().getMessage()).isEqualTo("연관된 데이터가 있어 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("UnauthorizedException 처리 시 올바른 응답을 반환한다")
    void handleUnauthorized() {
        // given
        UnauthorizedException ex = new UnauthorizedException();

        // when
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleUnauthorized(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(ApiResponse.Status.FAIL);
        assertThat(response.getBody().getMessage()).isEqualTo("인증이 필요합니다.");
    }

    @Test
    @DisplayName("처리되지 않은 예외는 500 에러로 처리된다")
    void handleException() {
        // given
        Exception ex = new RuntimeException("예상치 못한 오류");

        // when
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleException(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(ApiResponse.Status.FAIL);
        assertThat(response.getBody().getMessage()).isEqualTo("서버 오류가 발생했습니다.");
    }

    @Test
    @DisplayName("NoResourceFoundException 처리 시 404 응답을 반환한다")
    void handleNoResourceFoundException() {
        // given
        NoResourceFoundException ex = new NoResourceFoundException(null, "정적 리소스 없음");

        // when
        ResponseEntity<Void> response = globalExceptionHandler.handleNoResourceFoundException(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
