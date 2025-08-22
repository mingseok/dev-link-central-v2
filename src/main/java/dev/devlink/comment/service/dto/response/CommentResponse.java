package dev.devlink.comment.service.dto.response;

import dev.devlink.comment.entity.ArticleComment;
import dev.devlink.comment.entity.FeedComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private String writer;
    private Long writerId;
    private Long parentId;
    private List<CommentResponse> children = new ArrayList<>();
    private LocalDateTime createdAt;

    @Builder
    private CommentResponse(
            Long id,
            String content,
            String writer,
            Long writerId,
            Long parentId,
            LocalDateTime createdAt,
            List<CommentResponse> children
    ) {
        this.id = id;
        this.content = content;
        this.writer = writer;
        this.writerId = writerId;
        this.parentId = parentId;
        this.createdAt = createdAt;
        
        if (children != null) {
            this.children = children;
        }
    }

    public static CommentResponse from(ArticleComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getWriterNickname(),
                comment.getWriter().getId(),
                comment.getParentId(),
                comment.getCreatedAt(),
                new ArrayList<>()
        );
    }

    public static CommentResponse from(FeedComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getWriterNickname(),
                comment.getWriter().getId(),
                comment.getParentId(),
                comment.getCreatedAt(),
                new ArrayList<>()
        );
    }

    public void addChild(CommentResponse child) {
        this.children.add(child);
    }
}
