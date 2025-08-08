package dev.devlink.feed.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/feeds")
public class FeedViewController {

    @GetMapping
    public String feedList() {
        return "feed/list";
    }

    @GetMapping("/create")
    public String feedCreate() {
        return "feed/create";
    }
}
