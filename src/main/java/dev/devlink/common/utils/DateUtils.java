package dev.devlink.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String NO_DATE_INFO = "정보 없음";

    private DateUtils() {
        throw new AssertionError("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return DATETIME_FORMATTER.format(dateTime);
    }

    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return NO_DATE_INFO;
        }
        return DATE_FORMATTER.format(dateTime);
    }

    public static String formatFeedDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return DATETIME_FORMATTER.format(dateTime);
    }
}
