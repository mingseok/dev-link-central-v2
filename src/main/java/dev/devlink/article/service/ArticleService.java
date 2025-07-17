package dev.devlink.article.service;

import dev.devlink.article.controller.response.ArticleDetailResponse;
import dev.devlink.article.controller.response.ArticleListResponse;
import dev.devlink.article.entity.Article;
import dev.devlink.article.exception.ArticleError;
import dev.devlink.article.exception.ArticleException;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.article.service.command.ArticleCreateCommand;
import dev.devlink.article.service.command.ArticleUpdateCommand;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final MemberService memberService;
    private final ArticleViewService articleViewService;
    private final ArticleRepository articleRepository;

    @Transactional
    public void save(ArticleCreateCommand command) {
        Member member = memberService.findMemberById(command.getMemberId());
        Article article = Article.create(member, command.getTitle(), command.getContent());
        articleRepository.save(article);
    }

    @Transactional(readOnly = true)
    public ArticleDetailResponse findDetail(Long articleId, Long memberId) {
        Article article = articleRepository.findDetailById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));

        articleViewService.addViewCount(articleId);
        Long dbViewCount = article.getViewCount();
        Long totalViewCount = articleViewService.getTotalViewCount(articleId, dbViewCount);

        boolean isWriter = article.isAuthor(memberId);
        return ArticleDetailResponse.from(article, isWriter, totalViewCount);
    }

    @Transactional(readOnly = true)
    public Page<ArticleListResponse> findArticlesByPage(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Article> articlePage = articleRepository.findAllWithMember(sortedPageable);
        return articlePage.map(ArticleListResponse::from);
    }

    @Transactional
    public void update(ArticleUpdateCommand command) {
        Article article = findArticleById(command.getArticleId());
        article.checkAuthor(command.getMemberId());
        article.update(command.getTitle(), command.getContent());
    }

    @Transactional
    public void delete(Long articleId, Long memberId) {
        Article article = findArticleById(articleId);
        article.checkAuthor(memberId);
        articleRepository.delete(article);
    }

    public Article findArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));
    }
}
