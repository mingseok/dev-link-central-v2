package dev.devlink.feed.exception;

import dev.devlink.common.exception.CommonError;
import dev.devlink.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FeedError implements CommonError {

    NOT_FOUND(HttpStatus.NOT_FOUND, "40010", "피드를 찾을 수 없습니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "40410", "피드에 대한 권한이 없습니다."),
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "40011", "피드 내용이 유효하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
