package dev.devlink.article.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.devlink.article.entity.Article;
import dev.devlink.common.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticleDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private Long writerId;
    private String formattedCreatedAt;
    private LocalDateTime modifiedAt;

    @JsonProperty("isAuthor")
    private boolean author;

    public static ArticleDetailResponse from(Article article, boolean author) {
        return new ArticleDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getWriter(),
                article.getWriterId(),
                DateUtils.formatLocalDateTime(article.getCreatedAt()),
                article.getUpdatedAt(),
                author
        );
    }
}
