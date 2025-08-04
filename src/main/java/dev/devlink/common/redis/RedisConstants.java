package dev.devlink.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisConstants {

    public static final long SYNC_INTERVAL_MILLIS = 60_000L;      // Redis → DB 조회수 동기화 주기 (1분)
    public static final long RANKING_REFRESH_INTERVAL = 30_000L;  // 인기글 Top5 갱신 주기 (30초)
    public static final long DUPLICATE_PREVENTION_TTL = 300_000L; // 중복 조회 방지 TTL (5분)

    public static final long REDIS_SET_ADD_SUCCESS = 1L;
    public static final int TOP_LIMIT = 5;
    public static final int START_INDEX = 0;
}
