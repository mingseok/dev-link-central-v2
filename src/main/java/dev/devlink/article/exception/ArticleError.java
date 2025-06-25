package dev.devlink.article.exception;

import dev.devlink.common.exception.CommonError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ArticleError implements CommonError {

    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "40410", "존재하지 않는 게시글입니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "40311", "수정 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
