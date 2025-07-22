package dev.devlink.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisScripts {

    public static final String VIEW_COUNT_POP_SCRIPT = """
        local count = redis.call('GET', KEYS[1])
        if count then
            redis.call('DEL', KEYS[1])
            return count
        end
        """;
}
