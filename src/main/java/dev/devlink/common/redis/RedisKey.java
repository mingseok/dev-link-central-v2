package dev.devlink.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKey {

    private static final String ARTICLE_VIEW_PREFIX = "view:article:";
    private static final String ARTICLE_RANKING_KEY = "ranking:article:";
    private static final String CACHED_TOP_RANKING_KEY = "ranking:article:top5:cached";
    private static final String ARTICLE_LIKE_LOCK_PREFIX = "lock:article:like:";
    private static final String ARTICLE_VIEW_TRACKING_KEY_SET = "view:article:keys";
    private static final String ARTICLE_MEMBER_VIEW_PATTERN = "viewed:article:%s:member:%s";

    public static String articleViewKey(Long articleId) {
        return ARTICLE_VIEW_PREFIX + articleId;
    }

    public static String getArticleViewKey(Long articleId) {
        return ARTICLE_VIEW + articleId;
    }

    public static String getArticleViewKeys() {
        return ARTICLE_VIEW + "*";
    }

    public static String articleRanking() {
        return ARTICLE_RANKING_KEY;
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
