package dev.devlink.member.controller.closed;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.annotation.AuthMemberId;
import dev.devlink.member.controller.response.AuthenticatedMemberResponse;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthenticatedMemberResponse>> getAuthenticatedMember(
            @AuthMemberId Long memberId
    ) {
        AuthenticatedMemberResponse response = memberService.getAuthenticatedMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
