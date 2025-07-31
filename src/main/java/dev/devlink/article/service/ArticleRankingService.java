package dev.devlink.article.service;

import dev.devlink.article.entity.Article;
import dev.devlink.article.service.dto.response.ArticleListResponse;
import dev.devlink.common.redis.RedisConstants;
import dev.devlink.common.redis.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ArticleRankingService {

    private final StringRedisTemplate redisTemplate;

    public List<Long> getTopArticleIds() {
        Set<String> topArticleIds = redisTemplate.opsForZSet()
                .reverseRange(
                        RedisKey.getArticleViewSortedKey(),
                        RedisConstants.START_INDEX,
                        RedisConstants.TOP_LIMIT - 1
                );

        if (topArticleIds.isEmpty()) {
            return List.of();
        }

        return topArticleIds.stream()
                .map(Long::parseLong)
                .toList();
    }

    public ArticleListResponse buildArticleResponse(Long id, Article article) {
        long dbViewCount = article.getViewCount();
        String redisValue = redisTemplate.opsForValue()
                .get(RedisKey.getArticleViewKey(id));

        long cachedViewCount = 0L;
        if (redisValue != null) {
            cachedViewCount = Long.parseLong(redisValue);
        }

        long totalViewCount = dbViewCount + cachedViewCount;
        return ArticleListResponse.from(article, totalViewCount);
    }

    public void updateRankingScores() {
        Set<String> articleIds = redisTemplate.opsForSet()
                .members(RedisKey.flushTargetArticlesKey());

        if (articleIds == null) return;

        for (String articleIdStr : articleIds) {
            Long articleId = Long.parseLong(articleIdStr);
            String redisCountValue = redisTemplate.opsForValue()
                    .get(RedisKey.getArticleViewKey(articleId));

            if (redisCountValue == null) continue;

            long redisCount = Long.parseLong(redisCountValue);
            redisTemplate.opsForZSet().add(
                    RedisKey.getArticleViewSortedKey(),
                    articleIdStr,
                    (double) redisCount
            );
        }
    }
}
