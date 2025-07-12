package dev.devlink.article.controller.response;

import dev.devlink.article.entity.Article;
import dev.devlink.common.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleListResponse {
    private Long id;
    private String title;
    private String writer;
    private Long writerId;
    private String formattedCreatedAt;

    public static ArticleListResponse from(Article article) {
        return new ArticleListResponse(
                article.getId(),
                article.getTitle(),
                article.getWriterNickname(),
                article.getWriterId(),
                DateUtils.formatLocalDateTime(article.getCreatedAt())
        );
    }
}
