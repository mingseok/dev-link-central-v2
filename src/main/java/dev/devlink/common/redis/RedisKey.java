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

    public static String viewTrackingKeySet() {
        return ARTICLE_VIEW_TRACKING_KEY_SET;
    }

    public static String cachedTopRanking() {
        return CACHED_TOP_RANKING_KEY;
    }

    public static String articleRanking() {
        return ARTICLE_RANKING_KEY;
    }

    public static Long getArticleIdFromKey(String key) {
        return Long.parseLong(key.replace(ARTICLE_VIEW_PREFIX, ""));
    }

    public static String articleMemberViewKey(Long articleId, Long memberId) {
        return String.format(ARTICLE_MEMBER_VIEW_PATTERN, articleId, memberId);
    }

    public static String articleLikeLockKey(Long articleId, Long memberId) {
        return ARTICLE_LIKE_LOCK_PREFIX + articleId + ":" + memberId;
    }
}
