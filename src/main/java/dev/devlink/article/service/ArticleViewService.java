package dev.devlink.article.service;

import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.common.redis.RedisConstants;
import dev.devlink.common.redis.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final StringRedisTemplate redisTemplate;
    private final ArticleRepository articleRepository;

    public void increaseViewCount(Long articleId, Long memberId) {
        String memberViewSetKey = RedisKey.viewConcurrencyKey(articleId);
        String memberIdString = String.valueOf(memberId);

        Long addedCount = redisTemplate.opsForSet()
                .add(memberViewSetKey, memberIdString);

        boolean isVisit = false;
        if (addedCount == RedisConstants.ADD_SUCCESS) {
            isVisit = true;
        }

        if (isVisit) {
            redisTemplate.opsForValue()
                    .increment(RedisKey.getArticleViewKey(articleId));

            redisTemplate.opsForSet()
                    .add(RedisKey.flushTargetArticlesKey(), articleId.toString());

            redisTemplate.expire(
                    memberViewSetKey,
                    RedisConstants.DUPLICATE_PREVENTION_TTL,
                    TimeUnit.SECONDS
            );
        }
    }

    public Long getTotalViewCount(Long articleId, Long dbViewCount) {
        String redisViewCount = redisTemplate.opsForValue()
                .get(RedisKey.getArticleViewKey(articleId));

        if (redisViewCount == null) {
            return dbViewCount;
        }
        return dbViewCount + Long.parseLong(redisViewCount);
    }

    @Transactional
    @Scheduled(fixedRate = RedisConstants.SYNC_INTERVAL_MILLIS)
    public void bulkUpdateViewCounts() {
        Set<String> articleIds = redisTemplate.opsForSet()
                .members(RedisKey.flushTargetArticlesKey());

        if (articleIds == null) return;

        Map<Long, Long> redisViewCounts = new HashMap<>();

        for (String articleIdStr : articleIds) {
            Long articleId = Long.parseLong(articleIdStr);
            String viewCountStr = redisTemplate.opsForValue()
                    .get(RedisKey.getArticleViewKey(articleId));

            if (viewCountStr == null) continue;

            Long viewCount = Long.parseLong(viewCountStr);
            redisViewCounts.put(articleId, viewCount);
        }

        redisViewCounts.forEach(articleRepository::bulkAddViewCount);

        for (Long articleId : redisViewCounts.keySet()) {
            redisTemplate.delete(RedisKey.getArticleViewKey(articleId));

            redisTemplate.opsForSet()
                    .remove(RedisKey.flushTargetArticlesKey(), articleId.toString());
        }
    }
}
