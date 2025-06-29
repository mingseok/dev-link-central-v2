package dev.devlink.common.exception;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    // ServiceException
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceException(ServiceException ex) {
        CommonError commonError = ex.getCommonError();
        log.error("ServiceException 발생했습니다: {}", commonError.getMessage());
        return new ResponseEntity<>(
                ApiResponse.failure(commonError.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // 인증 오류 예외 처리
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        CommonError error = ex.getCommonError();
        log.warn("인증 오류 발생: {}", error.getMessage());
        return new ResponseEntity<>(
                ApiResponse.failure(error.getMessage()),
                error.getHttpStatus()
        );
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Unhandled Exception", ex);
        return new ResponseEntity<>(
                ApiResponse.failure("서버 오류가 발생했습니다."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("요청한 정적 리소스를 찾을 수 없습니다: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }
}
