package dev.devlink.member.controller.closed;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.member.controller.response.AuthenticatedMemberResponse;
import dev.devlink.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
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
            HttpServletRequest request
    ) {
        // TODO: 반복, 하드코딩 개선
        Long memberId = (Long) request.getAttribute("memberId");
        AuthenticatedMemberResponse response = memberService.getAuthenticatedMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
