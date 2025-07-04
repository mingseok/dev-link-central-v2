package dev.devlink.article.comment.controller.response;

import dev.devlink.article.comment.entity.Comment;
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
    private List<CommentResponse> children = new ArrayList<>();
    private LocalDateTime createdAt;

    private CommentResponse(
            Long id,
            String content,
            String writer,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.content = content;
        this.writer = writer;
        this.createdAt = createdAt;
        this.children = new ArrayList<>();
    }

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getWriterNickname(),
                comment.getCreatedAt()
        );
    }

    public void addChild(CommentResponse child) {
        this.children.add(child);
    }
}
