package dev.devlink.article.comment.service.command;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentCreateCommand {

    private final String content;
    private final Long parentId;
    private final Long memberId;
    private final Long articleId;

    @Builder
    public CommentCreateCommand(
            String content,
            Long parentId,
            Long memberId,
            Long articleId
    ) {
        this.content = content;
        this.parentId = parentId;
        this.memberId = memberId;
        this.articleId = articleId;
    }
}
