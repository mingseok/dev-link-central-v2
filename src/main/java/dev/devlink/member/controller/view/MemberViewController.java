package dev.devlink.member.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
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
