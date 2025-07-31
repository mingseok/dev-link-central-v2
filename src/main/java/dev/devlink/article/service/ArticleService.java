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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final MemberService memberService;
    private final ArticleRepository articleRepository;
    private final ArticleViewService articleViewService;
    private final ArticleRankingService articleRankingService;

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

        articleViewService.increaseViewCount(articleId, memberId);
        Long totalViewCount = articleViewService.getTotalViewCount(articleId, article.getViewCount());
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

    @Transactional(readOnly = true)
    public List<ArticleListResponse> findTopRankedArticles() {
        List<Long> topArticleIds = articleRankingService.getTopArticleIds();
        Map<Long, Article> articleMap = findArticlesByIds(topArticleIds);

        return topArticleIds.stream()
                .map(id -> articleRankingService.buildArticleResponse(
                        id, articleMap.get(id))
                )
                .filter(Objects::nonNull)
                .toList();
    }

    public Article findArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));
    }

    private Map<Long, Article> findArticlesByIds(List<Long> topArticleIds) {
        return articleRepository.findAllById(topArticleIds)
                .stream()
                .collect(Collectors.toMap(Article::getId, Function.identity()));
    }
}
