package dev.devlink.member.controller.open;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.member.controller.request.SignInRequest;
import dev.devlink.member.controller.request.SignUpRequest;
import dev.devlink.member.controller.response.JwtTokenResponse;
import dev.devlink.member.controller.response.SignUpResponse;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/members")
public class MemberPublicController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignUpResponse>> signup(
            @Validated @RequestBody SignUpRequest request
    ) {
        SignUpResponse response = memberService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> signin(
            @Validated @RequestBody SignInRequest request
    ) {
        JwtTokenResponse response = memberService.signin(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
