package dev.devlink.follow.controller.closed;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.annotation.AuthMemberId;
import dev.devlink.follow.service.FollowService;
import dev.devlink.follow.service.dto.request.FollowCreateRequest;
import dev.devlink.follow.service.dto.response.FollowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follows")
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> follow(
            @Validated @RequestBody FollowCreateRequest request,
            @AuthMemberId Long memberId
    ) {
        followService.follow(memberId, request);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @DeleteMapping("/{followeeId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long followeeId,
            @AuthMemberId Long memberId
    ) {
        followService.unfollow(memberId, followeeId);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<List<FollowResponse>>> getFollowers(
            @AuthMemberId Long memberId
    ) {
        List<FollowResponse> responses = followService.getFollowers(memberId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/following")
    public ResponseEntity<ApiResponse<List<FollowResponse>>> getFollowings(
            @AuthMemberId Long memberId
    ) {
        List<FollowResponse> responses = followService.getFollowings(memberId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
