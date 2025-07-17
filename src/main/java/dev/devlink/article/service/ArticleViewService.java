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

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final StringRedisTemplate redisTemplate;
    private final ArticleRepository articleRepository;

    public void addViewCount(Long articleId) {
        String key = RedisKey.articleViewKey(articleId);
        redisTemplate.opsForValue().increment(key);
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
    @Scheduled(fixedRate = RedisConstants.VIEW_SYNC_INTERVAL)
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
