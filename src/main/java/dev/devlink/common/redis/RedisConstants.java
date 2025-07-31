package dev.devlink.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisConstants {

    // 조회수 관련
    public static final long SYNC_INTERVAL_MILLIS = 300_000L;
    public static final long DUPLICATE_PREVENTION_TTL = 3600L;
    public static final long ADD_SUCCESS = 1L;

    // 랭킹 관련
    public static final long RANKING_REFRESH_INTERVAL = 30_000L; //600_000L;
    public static final int TOP_LIMIT = 5;
    public static final int START_INDEX = 0;
}
