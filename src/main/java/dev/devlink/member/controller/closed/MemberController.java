package dev.devlink.member.controller.closed;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.annotation.AuthMemberId;
import dev.devlink.member.service.MemberService;
import dev.devlink.member.service.dto.response.NicknameResponse;
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

    @GetMapping("/self")
    public ResponseEntity<ApiResponse<NicknameResponse>> findCurrentNickname(
            @AuthMemberId Long memberId
    ) {
        NicknameResponse response = memberService.findNicknameById(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
