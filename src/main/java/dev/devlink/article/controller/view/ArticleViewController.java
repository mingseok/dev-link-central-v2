package dev.devlink.article.controller.view;

import dev.devlink.article.controller.response.ArticleDetailsResponse;
import dev.devlink.article.controller.response.ArticleListResponse;
import dev.devlink.article.controller.response.PageNavigationInfo;
import dev.devlink.article.service.ArticleService;
import dev.devlink.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/articles")
public class ArticleViewController {

    private final MemberService memberService;
    private final ArticleService articleService;

    @GetMapping("/save")
    public String showSaveForm(Model model, HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        String nickname = memberService.findNicknameById(memberId);
        model.addAttribute("nickname", nickname);
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
