package dev.devlink.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKey {

    private static final String ARTICLE_VIEW = "article:view:";
    private static final String ARTICLE_VIEW_ZSET = "article:views";
    private static final String ARTICLE_VIEW_CONCURRENCY = "article:view:concurrency:";

    public static String getArticleViewKey(Long articleId) {
        return ARTICLE_VIEW + articleId;
    }

    public static String getArticleViewKeys() {
        return ARTICLE_VIEW + "*";
    }

    public static String getArticleViewSortedKey() {
        return ARTICLE_VIEW_ZSET;
    }

    public static Long getArticleId(String key) {
        return Long.parseLong(key.replace(ARTICLE_VIEW, ""));
    }

    public static String viewConcurrencyKey(Long articleId) {
        return ARTICLE_VIEW_CONCURRENCY + articleId;
    }

    public static String getViewConcurrencyPrefix() {
        return ARTICLE_VIEW_CONCURRENCY;
    }
}
