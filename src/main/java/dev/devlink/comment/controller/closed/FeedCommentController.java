package dev.devlink.comment.controller.closed;

import dev.devlink.comment.service.FeedCommentService;
import dev.devlink.comment.service.dto.request.CommentCreateRequest;
import dev.devlink.comment.service.dto.response.CommentResponse;
import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.annotation.AuthMemberId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feeds/{feedId}/comments")
@RequiredArgsConstructor
public class FeedCommentController {

    private final FeedCommentService feedCommentService;

    @PostMapping
    public ApiResponse<Void> createComment(
            @Valid @RequestBody CommentCreateRequest request,
            @PathVariable Long feedId,
            @AuthMemberId Long memberId
    ) {
        feedCommentService.save(request, feedId, memberId);
        return ApiResponse.successEmpty();
    }

    @GetMapping
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable Long feedId) {
        List<CommentResponse> comments = feedCommentService.getComments(feedId);
        return ApiResponse.success(comments);
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthMemberId Long memberId
    ) {
        feedCommentService.delete(commentId, memberId);
        return ApiResponse.successEmpty();
    }
}
