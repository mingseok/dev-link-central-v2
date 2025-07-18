package dev.devlink.comment.controller.open;

import dev.devlink.comment.service.CommentService;
import dev.devlink.comment.service.dto.response.CommentResponse;
import dev.devlink.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/articles/{articleId}/comments")
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long articleId) {
        List<CommentResponse> responses = commentService.getCommentTreeByArticleId(articleId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
