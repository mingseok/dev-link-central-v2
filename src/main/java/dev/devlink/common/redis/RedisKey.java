package dev.devlink.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKey {

    public static final String ARTICLE_VIEW_KEY_PATTERN = RedisPrefix.ARTICLE_VIEW.getValue() + "*";

    public static String articleViewKey(Long articleId) {
        return RedisPrefix.ARTICLE_VIEW.getValue() + articleId;
    }

    public static String articleViewKeyPattern() {
        return ARTICLE_VIEW_KEY_PATTERN;
    }

    public static String articleRanking() {
        return RedisPrefix.ARTICLE_RANKING.getValue();
    }

    public static Long extractArticleId(String key) {
        return Long.parseLong(key.replace(RedisPrefix.ARTICLE_VIEW.getValue(), ""));
    }
}
