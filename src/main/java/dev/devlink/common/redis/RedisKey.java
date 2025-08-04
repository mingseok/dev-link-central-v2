package dev.devlink.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKey {

    private static final String ARTICLE_VIEW = "article:view:";
    private static final String ARTICLE_TOP_LIST = "article:top:list";
    private static final String ARTICLE_VIEWED_MEMBERS = "article:viewed:members:";
    private static final String ARTICLES_SAVE_DB = "articles:save:db";

    public static String getArticleViewKey(Long articleId) {
        return ARTICLE_VIEW + articleId;
    }

    public static String getTopArticleListKey() {
        return ARTICLE_TOP_LIST;
    }

    public static String getViewedMembersKey(Long articleId) {
        return ARTICLE_VIEWED_MEMBERS + articleId;
    }

    public static String articlesSaveDbKey() {
        return ARTICLES_SAVE_DB;
    }
}
