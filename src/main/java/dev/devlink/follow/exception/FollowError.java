package dev.devlink.follow.exception;

import dev.devlink.common.exception.CommonError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowError implements CommonError {

    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "40010", "이미 팔로우한 사용자입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "40410", "팔로우 관계가 존재하지 않습니다."),
    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "40011", "자기 자신은 팔로우할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
