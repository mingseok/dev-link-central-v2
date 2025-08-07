package dev.devlink.follow.constant;

import java.time.format.DateTimeFormatter;

public class FollowConstants {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final String NO_DATE_INFO = "정보 없음";
    
    private FollowConstants() {
        throw new AssertionError("상수 클래스는 인스턴스화할 수 없습니다.");
    }
}
