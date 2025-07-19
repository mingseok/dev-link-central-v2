package dev.devlink.article.service.dto.response;

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
    private Long viewCount;

    public static ArticleListResponse from(Article article) {
        return new ArticleListResponse(
                article.getId(),
                article.getTitle(),
                article.getWriterNickname(),
                article.getWriterId(),
                DateUtils.formatLocalDateTime(article.getCreatedAt()),
                article.getViewCount()
        );
    }

    public static ArticleListResponse from(Article article, Long totalViewCount) {
        return new ArticleListResponse(
                article.getId(),
                article.getTitle(),
                article.getWriterNickname(),
                article.getWriterId(),
                DateUtils.formatLocalDateTime(article.getCreatedAt()),
                totalViewCount
        );
    }
}
