package dev.devlink.common.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisPrefix {

    ARTICLE_VIEW("view:article:"),
    ARTICLE_RANKING("ranking:article:"),
    ARTICLE_MEMBER_VIEW("viewed:article:%s:member:%s");

    private final String value;
}
