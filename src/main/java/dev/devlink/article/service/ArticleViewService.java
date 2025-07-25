package dev.devlink.article.service;

import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.common.redis.RedisConstants;
import dev.devlink.common.redis.RedisKey;
import dev.devlink.common.redis.RedisScripts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final StringRedisTemplate redisTemplate;
    private final ArticleRepository articleRepository;

    public Long addViewAndCount(Long articleId, Long memberId, Long dbViewCount) {
        boolean isFirstVisit = addUniqueViewCount(articleId, memberId);
        if (isFirstVisit) {
            redisTemplate.opsForZSet().incrementScore(
                    RedisKey.articleRanking(),
                    articleId.toString(),
                    RedisConstants.SCORE
            );
        }
        return getTotalViewCount(articleId, dbViewCount);
    }

    public boolean addUniqueViewCount(Long articleId, Long memberId) {
        String memberViewKey = RedisKey.articleMemberViewKey(articleId, memberId);
        Boolean isFirstVisit = redisTemplate.opsForValue()
                .setIfAbsent(
                        memberViewKey,
                        RedisConstants.TRACKING_MARKER,
                        RedisConstants.DUPLICATE_PREVENTION_TTL,
                        TimeUnit.SECONDS
                );

        if (Boolean.TRUE.equals(isFirstVisit)) {
            redisTemplate.opsForValue().increment(RedisKey.articleViewKey(articleId));
            return true;
        }
        return false;
    }

    public Long getTotalViewCount(Long articleId, Long dbViewCount) {
        String key = RedisKey.articleViewKey(articleId);
        String cachedCount = redisTemplate.opsForValue().get(key);

        if (cachedCount == null) {
            return dbViewCount;
        }
        return dbViewCount + Long.parseLong(cachedCount);
    }

    @Transactional
    @Scheduled(fixedRate = RedisConstants.SYNC_INTERVAL_MILLIS)
    public void bulkUpdateViewCounts() {
        Set<String> keys = redisTemplate.opsForSet().members(RedisKey.viewTrackingKeySet());
        if (keys == null || keys.isEmpty()) return;

        Map<Long, Long> pendingViewCounts = new HashMap<>();

        for (String key : keys) {
            consumeViewCount(key).ifPresent(countStr -> {
                Long articleId = RedisKey.getArticleIdFromKey(key);
                Long redisViewCount = Long.parseLong(countStr);
                pendingViewCounts.put(articleId, redisViewCount);
                redisTemplate.opsForSet().remove(RedisKey.viewTrackingKeySet(), key);
            });
        }

        pendingViewCounts.forEach(articleRepository::bulkAddViewCount);
    }

    public Optional<String> consumeViewCount(String key) {
        Object result = redisTemplate.execute(
                (RedisCallback<Object>) connection -> connection.eval(
                        RedisScripts.VIEW_COUNT_POP_SCRIPT.getBytes(),
                        ReturnType.VALUE,
                        RedisConstants.SINGLE_KEY,
                        key.getBytes()
                )
        );
        return Optional.ofNullable(result).map(Object::toString);
    }
}
