package dev.devlink.article.service;

import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.common.redis.RedisConstants;
import dev.devlink.common.redis.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final StringRedisTemplate redisTemplate;
    private final ArticleRepository articleRepository;

    public boolean addView(Long articleId, Long memberId) {
        return addViewIfFirstVisit(articleId, memberId);
    }

    private boolean addViewIfFirstVisit(Long articleId, Long memberId) {
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
    public void flushViewCount() {
        String keyPattern = RedisKey.articleViewKeyPattern();
        Set<String> keys = redisTemplate.keys(keyPattern);

        if (keys.isEmpty()) return;

        for (String key : keys) {
            Long articleId = RedisKey.extractArticleId(key);
            String countStr = redisTemplate.opsForValue().get(key);
            if (countStr == null) continue;

            Long redisViewCount = Long.parseLong(countStr);

            articleRepository.findById(articleId)
                    .ifPresent(article -> {
                        article.addViewCount(redisViewCount);
                        redisTemplate.delete(key);
                    });
        }
    }
}
