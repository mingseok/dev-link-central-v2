package dev.devlink.comment.exception;

import dev.devlink.common.exception.CommonError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentError implements CommonError {

    NOT_FOUND(HttpStatus.NOT_FOUND, "40420", "존재하지 않는 댓글입니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "40321", "댓글을 삭제할 권한이 없습니다."),
    HAS_CHILD_COMMENTS(HttpStatus.BAD_REQUEST, "40022", "대댓글이 있어 삭제할 수 없습니다."),
    PARENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "40030", "부모 댓글이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
