package dev.devlink.article.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ArticleCreateServiceDto {

    private final String title;
    private final String content;
    private final Long memberId;

    @Builder
    public ArticleCreateServiceDto(
            Long memberId,
            String title,
            String content
    ) {
        this.title = title;
        this.content = content;
        this.memberId = memberId;
    }
}
