package dev.devlink.article.controller.view;

import dev.devlink.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/articles")
public class ArticleViewController {

    private final MemberService memberService;

    @GetMapping("/save")
    public String showArticleSaveForm(Model model, HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        String nickname = memberService.findNicknameById(memberId);
        model.addAttribute("nickname", nickname);
        return "articles/save";
    }
}
