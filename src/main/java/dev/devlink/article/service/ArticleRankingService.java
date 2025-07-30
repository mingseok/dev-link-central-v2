package dev.devlink.article.service;

import dev.devlink.article.entity.Article;
import dev.devlink.article.service.dto.response.ArticleListResponse;
import dev.devlink.common.redis.RedisConstants;
import dev.devlink.common.redis.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ArticleRankingService {

    private final StringRedisTemplate redisTemplate;

    public List<Long> getTopArticleIds() {
        Set<String> TopArticleIds = redisTemplate.opsForZSet()
                .reverseRange(
                        RedisKey.getArticleViewSortedKey(),
                        RedisConstants.START_INDEX,
                        RedisConstants.TOP_LIMIT - 1
                );

        if (TopArticleIds.isEmpty()) {
            return List.of();
        }

        return TopArticleIds.stream()
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

    @Scheduled(fixedRate = RedisConstants.RANKING_REFRESH_INTERVAL)
    public void updateViewRankingScores() {
        Set<String> keys = redisTemplate.keys(RedisKey.getArticleViewKeys());
        if (keys.isEmpty()) return;

        for (String key : keys) {
            if (key.startsWith(RedisKey.getViewConcurrencyPrefix())) continue;

            Long articleId = RedisKey.getArticleId(key);
            String redisCountValue = redisTemplate.opsForValue().get(key);

            long redisCount = 0L;
            if (redisCountValue != null) {
                redisCount = Long.parseLong(redisCountValue);
            }

            redisTemplate.opsForZSet().add(
                    RedisKey.getArticleViewSortedKey(),
                    String.valueOf(articleId),
                    (double) redisCount
            );
        }
    }
}
