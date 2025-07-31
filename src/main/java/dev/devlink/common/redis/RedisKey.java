package dev.devlink.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKey {

    private static final String ARTICLE_VIEW = "article:view:";
    private static final String ARTICLE_VIEW_ZSET = "article:views";
    private static final String ARTICLE_VIEW_CONCURRENCY = "article:view:concurrency:";
    private static final String ARTICLE_VIEW_FLUSH_TARGETS = "article:view:flush:targets";

    public static String getArticleViewKey(Long articleId) {
        return ARTICLE_VIEW + articleId;
    }

    public static String getArticleViewSortedKey() {
        return ARTICLE_VIEW_ZSET;
    }

    public static String viewConcurrencyKey(Long articleId) {
        return ARTICLE_VIEW_CONCURRENCY + articleId;
    }

    public static String flushTargetArticlesKey() {
        return ARTICLE_VIEW_FLUSH_TARGETS;
    }
}
