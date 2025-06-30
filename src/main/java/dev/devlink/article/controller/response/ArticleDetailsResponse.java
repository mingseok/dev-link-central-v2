package dev.devlink.article.controller.response;

import dev.devlink.article.entity.Article;
import dev.devlink.common.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticleDetailsResponse {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private Long writerId;
    private String formattedCreatedAt;
    private LocalDateTime modifiedAt;
    private boolean isWriter;

    public static ArticleDetailsResponse from(Article article, boolean isWriter) {
        return new ArticleDetailsResponse(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getWriter(),
                article.getWriterId(),
                DateUtils.formatLocalDateTime(article.getCreatedAt()),
                article.getUpdatedAt(),
                isWriter
        );
    }
}
