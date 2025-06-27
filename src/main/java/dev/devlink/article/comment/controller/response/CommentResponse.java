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

    public static CommentResponse from(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.id = comment.getId();
        response.content = comment.getContent();
        response.writer = comment.getMember().getNickname();
        response.createdAt = comment.getCreatedAt();
        return response;
    }

    public void addChild(CommentResponse child) {
        this.children.add(child);
    }
}
