package dev.devlink.comment.controller.closed;

import dev.devlink.comment.service.ArticleCommentService;
import dev.devlink.comment.service.dto.request.CommentCreateRequest;
import dev.devlink.comment.service.dto.response.CommentResponse;
import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.annotation.AuthMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles/{articleId}/comments")
@RequiredArgsConstructor
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(
            @Validated @RequestBody CommentCreateRequest request,
            @PathVariable Long articleId,
            @AuthMemberId Long memberId
    ) {
        articleCommentService.save(request, articleId, memberId);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @GetMapping
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable Long articleId) {
        List<CommentResponse> responses = articleCommentService.getComments(articleId);
        return ApiResponse.success(responses);
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthMemberId Long memberId
    ) {
        articleCommentService.delete(commentId, memberId);
        return ApiResponse.successEmpty();
    }
}
