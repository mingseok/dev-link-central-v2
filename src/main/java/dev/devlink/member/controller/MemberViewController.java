package dev.devlink.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/members")
public class MemberViewController {

    @GetMapping("/signup")
    public String showSignup() {
        return "/members/sign-up";
    }
}
