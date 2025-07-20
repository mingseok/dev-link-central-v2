package dev.devlink.article.exception;

import dev.devlink.common.exception.CommonError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ArticleError implements CommonError {

    // Article 관련
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "40410", "존재하지 않는 게시글입니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "40311", "수정 권한이 없습니다."),

    // ArticleLike 관련
    CONCURRENT_LIKE_REQUEST(HttpStatus.CONFLICT, "40911", "동시 요청으로 인해 좋아요 처리가 실패했습니다."),
    LOCK_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "50020", "좋아요 처리 중 인터럽트가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
