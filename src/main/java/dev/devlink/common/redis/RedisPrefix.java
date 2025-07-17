package dev.devlink.common.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisPrefix {

    ARTICLE_VIEW("view:article:"),
    ARTICLE_RANKING("ranking:article:");

    private final String value;
}
