package dev.devlink.article.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/articles")
public class ArticleViewController {

    @GetMapping("/save")
    public String showSaveForm() {
        return "articles/save";
    }

    @GetMapping("/{id}")
    public String showDetailPage() {
        return "articles/detail";
    }

    @GetMapping("/paging")
    public String showPagedArticles() {
        return "/articles/paging";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm() {
        return "/articles/update";
    }
}
