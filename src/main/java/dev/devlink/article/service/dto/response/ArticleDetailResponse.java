package dev.devlink.article.service.dto.response;

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
    private Long viewsCount;

    @JsonProperty("isAuthor")
    private boolean author;

    public static ArticleDetailResponse from(Article article, boolean author, Long viewsCount) {
        return new ArticleDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getWriterNickname(),
                article.getWriterId(),
                DateUtils.formatLocalDateTime(article.getCreatedAt()),
                article.getUpdatedAt(),
                viewsCount,
                author
        );
    }
}
