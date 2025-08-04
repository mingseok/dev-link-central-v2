package dev.devlink.article.service;

import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.common.redis.RedisConstants;
import dev.devlink.common.redis.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final StringRedisTemplate redisTemplate;
    private final ArticleRepository articleRepository;

    public void increaseViewCount(Long articleId, Long memberId) {
        String key = RedisKey.getViewedMembersKey(articleId);
        Long added = redisTemplate.opsForSet().add(key, String.valueOf(memberId));

        if (added != RedisConstants.REDIS_SET_ADD_SUCCESS) {
            return;
        }
        redisTemplate.expire(key, RedisConstants.DUPLICATE_PREVENTION_TTL, TimeUnit.SECONDS);
        redisTemplate.opsForValue().increment(RedisKey.getArticleViewKey(articleId));
        redisTemplate.opsForSet().add(RedisKey.articlesSaveDbKey(), articleId.toString());
    }

    public Long getTotalViewCount(Long articleId, Long dbViewCount) {
        String redisValue = redisTemplate.opsForValue().get(RedisKey.getArticleViewKey(articleId));

        long redisCount = 0L;
        if (redisValue != null) {
            redisCount = Long.parseLong(redisValue);
        }
        return dbViewCount + redisCount;
    }

    @Transactional
    public void bulkUpdateViewCounts() {
        Set<String> articleIds = redisTemplate.opsForSet().members(RedisKey.articlesSaveDbKey());
        if (articleIds == null) return;

        Map<Long, Long> redisViewCounts = getViewCountsFromRedis(articleIds);
        if (redisViewCounts.isEmpty()) return;

        redisViewCounts.forEach(articleRepository::bulkAddViewCount);
        articleRepository.flush();

        for (Long articleId : redisViewCounts.keySet()) {
            redisTemplate.delete(RedisKey.getArticleViewKey(articleId));
            redisTemplate.opsForSet().remove(RedisKey.articlesSaveDbKey(), articleId.toString());
        }
    }

    private Map<Long, Long> getViewCountsFromRedis(Set<String> articleIds) {
        Map<Long, Long> result = new HashMap<>();

        for (String articleIdStr : articleIds) {
            String viewCountValue = redisTemplate.opsForValue()
                    .get(RedisKey.getArticleViewKey(Long.parseLong(articleIdStr)));

            if (viewCountValue != null) {
                result.put(Long.parseLong(articleIdStr), Long.parseLong(viewCountValue));
            }
        }
        return result;
    }
}
