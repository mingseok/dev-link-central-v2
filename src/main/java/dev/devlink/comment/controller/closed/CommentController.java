package dev.devlink.comment.controller.closed;

import dev.devlink.comment.controller.request.CommentCreateRequest;
import dev.devlink.comment.service.CommentService;
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
            @Validated @RequestBody CommentCreateRequest request,
            @PathVariable Long articleId,
            @AuthMemberId Long memberId
    ) {
        commentService.save(request.toServiceDto(articleId, memberId));
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
