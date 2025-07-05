package dev.devlink.article.service;

import dev.devlink.article.controller.response.ArticleDetailResponse;
import dev.devlink.article.controller.response.ArticleListResponse;
import dev.devlink.article.entity.Article;
import dev.devlink.article.exception.ArticleError;
import dev.devlink.article.exception.ArticleException;
import dev.devlink.article.repository.ArticleRepository;
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
    private final ArticleRepository articleRepository;

    @Transactional
    public void save(Long memberId, String title, String content) {
        Member member = memberService.findMemberById(memberId);
        Article article = Article.create(member, title, content);
        articleRepository.save(article);
    }

    @Transactional(readOnly = true)
    public ArticleDetailResponse findDetail(Long articleId, Long memberId) {
        Article article = articleRepository.findDetailById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));

        boolean isWriter = article.isAuthor(memberId);
        return ArticleDetailResponse.from(article, isWriter);
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
    public void update(String title, String content, Long articleId, Long memberId) {
        Article article = getArticleForUpdate(articleId, memberId);
        article.update(title, content);
    }

    @Transactional
    public void delete(Long articleId, Long memberId) {
        Article article = getArticleForDelete(articleId, memberId);
        articleRepository.delete(article);
    }

    private Article findArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));
    }

    private Article getArticleForUpdate(Long articleId, Long memberId) {
        Article article = findArticleById(articleId);
        article.checkAuthor(memberId);
        return article;
    }

    private Article getArticleForDelete(Long articleId, Long memberId) {
        Article article = findArticleById(articleId);
        article.checkAuthor(memberId);
        return article;
    }

    public Article findArticleRequired(Long articleId) {
        return findArticleById(articleId);
    }
}
