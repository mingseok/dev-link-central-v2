package dev.devlink.common.exception;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceException(ServiceException ex) {
        CommonError commonError = ex.getCommonError();
        log.warn("ServiceException 발생: {}", commonError.getMessage());
        return new ResponseEntity<>(
                ApiResponse.failure(commonError.getMessage()),
                commonError.getHttpStatus()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = ErrorCode.BAD_REQUEST.getMessage();
        if (fieldError != null) {
            errorMessage = fieldError.getDefaultMessage();
        }

        log.warn("MethodArgumentNotValidException 발생: {}", errorMessage);
        return new ResponseEntity<>(
                ApiResponse.failure(errorMessage),
                ErrorCode.BAD_REQUEST.getHttpStatus()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataViolation(DataIntegrityViolationException ex) {
        log.warn("DataIntegrityViolationException 발생: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.failure("연관된 데이터가 있어 삭제할 수 없습니다."),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        CommonError error = ex.getCommonError();
        log.warn("인증 오류 발생: {}", error.getMessage());
        return new ResponseEntity<>(
                ApiResponse.failure(error.getMessage()),
                error.getHttpStatus()
        );
    }

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
