package dev.devlink.article.service.command;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ArticleUpdateCommand {

    private final String title;
    private final String content;
    private final Long articleId;
    private final Long memberId;

    @Builder
    public ArticleUpdateCommand(
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
