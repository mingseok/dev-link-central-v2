package dev.devlink.article.comment.controller.closed;

import dev.devlink.article.comment.controller.request.CommentCreateRequest;
import dev.devlink.article.comment.service.CommentService;
import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.annotation.AuthMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles/{articleId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(
            @PathVariable Long articleId,
            @Validated @RequestBody CommentCreateRequest request,
            @AuthMemberId Long memberId
    ) {
        commentService.save(memberId, articleId, request);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long commentId,
            @AuthMemberId Long memberId
    ) {
        commentService.delete(commentId, memberId);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }
}
