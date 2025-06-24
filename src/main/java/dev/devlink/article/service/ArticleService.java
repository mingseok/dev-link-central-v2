package dev.devlink.article.service;

import dev.devlink.article.controller.request.ArticleCreateRequest;
import dev.devlink.article.entity.Article;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private final MemberService memberService;
    private final ArticleRepository articleRepository;

    @Transactional
    public void save(ArticleCreateRequest request, Long memberId) {
        Member member = memberService.getMemberById(memberId);
        Article article = Article.create(member, request.getTitle(), request.getContent());
        articleRepository.save(article);
    }
}
