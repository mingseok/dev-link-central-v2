package dev.devlink.article.service;

import dev.devlink.article.constant.ArticleConstants;
import dev.devlink.article.entity.Article;
import dev.devlink.article.exception.ArticleError;
import dev.devlink.article.exception.ArticleException;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.article.service.dto.request.ArticleCreateRequest;
import dev.devlink.article.service.dto.request.ArticleUpdateRequest;
import dev.devlink.article.service.dto.response.ArticleDetailResponse;
import dev.devlink.article.service.dto.response.ArticleListResponse;
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
    private final ArticleViewService articleViewService;

    @Transactional
    public void save(ArticleCreateRequest createRequest, Long memberId) {
        Member member = memberService.findMemberById(memberId);
        Article article = Article.create(member, createRequest.getTitle(), createRequest.getContent());
        articleRepository.save(article);
    }

    @Transactional(readOnly = true)
    public ArticleDetailResponse findDetail(Long articleId, Long memberId) {
        Article article = articleRepository.findDetailById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));

        Long totalViewCount = articleViewService.addViewAndCount(
                articleId, memberId, article.getViewCount());

        return ArticleDetailResponse.from(article, memberId, totalViewCount);
    }

    @Transactional(readOnly = true)
    public Page<ArticleListResponse> findArticlesByPage(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, ArticleConstants.SORT_BY_ID)
        );

        Page<Article> articlePage = articleRepository.findAllWithMember(sortedPageable);
        return articlePage.map(ArticleListResponse::from);
    }

    @Transactional
    public void update(ArticleUpdateRequest updateRequest, Long articleId, Long memberId) {
        Article article = findArticleById(articleId);
        article.checkAuthor(memberId);
        article.update(updateRequest.getTitle(), updateRequest.getContent());
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
