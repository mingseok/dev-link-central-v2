package dev.devlink.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisConstants {

    public static final long VIEW_SYNC_INTERVAL = 600_000L;
    public static final long VIEW_DUPLICATE_PREVENTION_TTL = 3600L;
    public static final String VIEW_TRACKING_MARKER = "VIEWED";
}
