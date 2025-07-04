package dev.devlink.member.controller.view;

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
    public String showSignUpForm() {
        return "/members/sign-up";
    }

    @GetMapping("/signin")
    public String showSignInForm() {
        return "/members/sign-in";
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "/members/home";
    }
}
