package dev.devlink.article.service;

import dev.devlink.article.entity.Article;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.article.service.dto.response.ArticleListResponse;
import dev.devlink.common.redis.RedisConstants;
import dev.devlink.common.redis.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleRankingService {

    private final StringRedisTemplate redisTemplate;
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public List<ArticleListResponse> getTopRankedArticles() {
        List<Long> topIds = getTopArticleIds();
        Map<Long, Article> articleMap = sortedArticleMap(topIds);

        return topIds.stream()
                .map(articleMap::get)
                .filter(Objects::nonNull)
                .map(article -> ArticleListResponse.from(article, article.getViewCount()))
                .toList();
    }

    @Transactional(readOnly = true)
    public void updateRedisTopArticles() {
        List<Article> topArticles = articleRepository.findTopByViews(
                PageRequest.of(RedisConstants.START_INDEX, RedisConstants.TOP_LIMIT));

        String key = RedisKey.getTopArticleListKey();

        redisTemplate.delete(key);
        for (Article article : topArticles) {
            redisTemplate.opsForList().rightPush(key, article.getIdAsString());
        }
    }

    private List<Long> getTopArticleIds() {
        String key = RedisKey.getTopArticleListKey();
        List<String> topArticleIds = redisTemplate.opsForList()
                .range(key, RedisConstants.START_INDEX, RedisConstants.TOP_LIMIT - 1);

        if (topArticleIds == null) {
            return List.of();
        }

        return topArticleIds.stream()
                .map(Long::parseLong)
                .toList();
    }

    private Map<Long, Article> sortedArticleMap(List<Long> topIds) {
        return articleRepository.findAllById(topIds).stream()
                .sorted(Comparator.comparingInt(article -> topIds.indexOf(article.getId())))
                .collect(Collectors.toMap(
                        Article::getId,
                        Function.identity(),
                        (existing, replacement) -> replacement,
                        LinkedHashMap::new
                ));
    }
}
