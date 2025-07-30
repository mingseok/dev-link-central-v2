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
        Set<String> keys = redisTemplate.keys(RedisKey.getArticleViewKeys());
        if (keys.isEmpty()) return;

        Map<Long, Long> redisViewCounts = new HashMap<>();

        for (String key : keys) {
            if (key.startsWith(RedisKey.getViewConcurrencyPrefix())) continue;

            String value = redisTemplate.opsForValue().get(key);
            Long articleId = RedisKey.getArticleId(key);
            Long viewCount = Long.parseLong(value);

            redisViewCounts.put(articleId, viewCount);
        }

        redisViewCounts.forEach(articleRepository::bulkAddViewCount);
    }
}
