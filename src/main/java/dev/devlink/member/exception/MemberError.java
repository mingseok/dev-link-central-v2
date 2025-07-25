package dev.devlink.member.exception;

import dev.devlink.common.exception.CommonError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberError implements CommonError {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "40401", "존재하지 않는 회원입니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "40402", "존재하지 않는 회원 이메일입니다."),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "40003", "이미 등록된 이메일입니다."),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "40004", "이미 사용 중인 닉네임입니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "40005", "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "40301", "접근 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
