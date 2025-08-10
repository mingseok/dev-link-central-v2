package dev.devlink.follow.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/follow")
public class FollowViewController {

    @GetMapping("/followers")
    public String followers() {
        return "profile/followers";
    }

    @GetMapping("/following")
    public String following() {
        return "profile/following";
    }
}
