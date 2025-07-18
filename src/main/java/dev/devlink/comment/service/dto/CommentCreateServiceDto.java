package dev.devlink.comment.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentCreateServiceDto {

    private final String content;
    private final Long parentId;
    private final Long memberId;
    private final Long articleId;

    @Builder
    public CommentCreateServiceDto(
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
