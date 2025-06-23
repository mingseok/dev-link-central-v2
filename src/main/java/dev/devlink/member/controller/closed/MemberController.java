package dev.devlink.member.controller.closed;

import dev.devlink.common.jwt.JwtTokenProvider;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;


}
