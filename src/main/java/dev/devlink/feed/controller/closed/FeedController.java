package dev.devlink.feed.controller.closed;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.annotation.AuthMemberId;
import dev.devlink.feed.service.FeedService;
import dev.devlink.feed.service.dto.request.FeedCreateRequest;
import dev.devlink.feed.service.dto.response.FeedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createFeed(
            @Validated @RequestBody FeedCreateRequest request,
            @AuthMemberId Long memberId
    ) {
        feedService.createFeed(memberId, request);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedResponse>>> getFeeds(
            @AuthMemberId Long memberId
    ) {
        List<FeedResponse> responses = feedService.getFeeds(memberId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<ApiResponse<Void>> deleteFeed(
            @PathVariable Long feedId,
            @AuthMemberId Long memberId
    ) {
        feedService.deleteFeed(memberId, feedId);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }
}
