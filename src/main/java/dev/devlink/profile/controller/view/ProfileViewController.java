package dev.devlink.profile.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/profile")
public class ProfileViewController {

    @GetMapping("/{id}")
    public String showProfilePage() {
        return "/profile/detail";
    }
}
