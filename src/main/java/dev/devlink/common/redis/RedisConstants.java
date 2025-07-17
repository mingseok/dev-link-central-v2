package dev.devlink.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisConstants {

    public static final long DEFAULT_TTL_SECONDS = 300;
    public static final long NO_EXPIRE = -1;
    public static final long DEFAULT_VIEW_COUNT = 0L;
    public static final long VIEW_SYNC_INTERVAL = 600_000L;
}
