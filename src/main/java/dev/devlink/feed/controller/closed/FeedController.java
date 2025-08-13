package dev.devlink.feed.controller.closed;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.annotation.AuthMemberId;
import dev.devlink.feed.service.FeedLikeService;
import dev.devlink.feed.service.FeedService;
import dev.devlink.feed.service.dto.response.FeedLikeResponse;
import dev.devlink.feed.service.dto.response.FeedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {

    private final FeedService feedService;
    private final FeedLikeService feedLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createFeed(
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile image,
            @AuthMemberId Long memberId
    ) {
        feedService.createFeed(memberId, content, image);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedResponse>>> getFeeds(
            @AuthMemberId Long memberId
    ) {
        List<FeedResponse> responses = feedService.getFeeds(memberId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFeed(
            @PathVariable Long id,
            @AuthMemberId Long memberId
    ) {
        feedService.deleteFeed(memberId, id);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<FeedLikeResponse>> likeOrUnlike(
            @PathVariable Long id,
            @AuthMemberId Long memberId
    ) {
        FeedLikeResponse response = feedLikeService.likeOrUnlike(memberId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
