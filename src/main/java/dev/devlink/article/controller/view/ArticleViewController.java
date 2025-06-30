package dev.devlink.article.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/view/articles")
public class ArticleViewController {

    @GetMapping("/save")
    public String showSaveForm() {
        return "articles/save";
    }

    @GetMapping("/{id}")
    public String showDetailPage(@PathVariable Long id, Model model) {
        ArticleDetailsResponse response = articleService.findDetail(id);
        model.addAttribute("article", response);
        return "articles/detail";
    }

    @GetMapping("/paging")
    public String showPagedArticles(@PageableDefault(size = 8) Pageable pageable, Model model) {
        Page<ArticleListResponse> articlePage = articleService.findArticlesByPage(pageable);
        List<ArticleListResponse> articleList = articlePage.getContent();
        PageNavigationInfo pageInfo = articleService.getPageNavigation(articlePage);

        model.addAttribute("articleList", articleList);
        model.addAttribute("articlePage", articlePage);
        model.addAttribute("startPage", pageInfo.getStartPage());
        model.addAttribute("endPage", pageInfo.getEndPage());
        return "/articles/paging";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        ArticleDetailsResponse article = articleService.findDetail(id);
        model.addAttribute("articleUpdate", article);
        return "/articles/update";
    }
}
