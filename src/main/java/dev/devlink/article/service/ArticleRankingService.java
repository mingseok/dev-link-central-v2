package dev.devlink.article.service;

import dev.devlink.article.entity.Article;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.article.service.dto.response.ArticleListResponse;
import dev.devlink.common.redis.RedisConstants;
import dev.devlink.common.redis.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleRankingService {

    private final StringRedisTemplate redisTemplate;
    private final ArticleRepository articleRepository;
    private final ArticleViewService articleViewService;

    @Transactional(readOnly = true)
    public List<ArticleListResponse> findTopRankedArticles() {
        List<Long> topArticleIds = getTopFiveArticleIds();
        Map<Long, Article> articleMap = articleRepository.findAllById(topArticleIds)
                .stream()
                .collect(Collectors.toMap(Article::getId, Function.identity()));

        return topArticleIds.stream()
                .map(id -> createResponse(id, articleMap.get(id)))
                .filter(Objects::nonNull)
                .toList();
    }

    private ArticleListResponse createResponse(Long id, Article article) {
        if (article == null) return null;

        Long dbViewCount = article.getViewCount();
        Long totalViewCount = articleViewService.getTotalViewCount(id, dbViewCount);
        return ArticleListResponse.from(article, totalViewCount);
    }

    public List<Long> getTopFiveArticleIds() {
        return redisTemplate.opsForZSet()
                .reverseRange(RedisKey.articleRanking(), 0, RedisConstants.TOP_LIMIT - 1)
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
