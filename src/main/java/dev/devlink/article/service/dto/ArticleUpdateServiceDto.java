package dev.devlink.article.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ArticleUpdateServiceDto {

    private final String title;
    private final String content;
    private final Long articleId;
    private final Long memberId;

    @Builder
    public ArticleUpdateServiceDto(
            String title,
            String content,
            Long memberId,
            Long articleId
    ) {
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.articleId = articleId;
    }
}
