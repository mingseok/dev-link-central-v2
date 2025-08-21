package dev.devlink.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateUtilsTest {

    @Test
    @DisplayName("LocalDateTime을 yyyy.MM.dd HH:mm 형식으로 포맷한다")
    void formatLocalDateTime_Success() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 14, 30, 45);

        // when
        String formatted = DateUtils.formatLocalDateTime(dateTime);

        // then
        assertThat(formatted).isEqualTo("2023.12.25 14:30");
    }

    @Test
    @DisplayName("LocalDateTime이 null이면 null을 반환한다")
    void formatLocalDateTime_WithNull_ReturnsNull() {
        // when
        String formatted = DateUtils.formatLocalDateTime(null);

        // then
        assertThat(formatted).isNull();
    }

    @Test
    @DisplayName("LocalDateTime을 yyyy-MM-dd 형식으로 포맷한다")
    void formatDate_Success() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 15, 9, 45, 30);

        // when
        String formatted = DateUtils.formatDate(dateTime);

        // then
        assertThat(formatted).isEqualTo("2023-05-15");
    }

    @Test
    @DisplayName("날짜가 null이면 '정보 없음'을 반환한다")
    void formatDate_WithNull_ReturnsNoInfo() {
        // when
        String formatted = DateUtils.formatDate(null);

        // then
        assertThat(formatted).isEqualTo("정보 없음");
    }

    @Test
    @DisplayName("피드용 날짜 포맷을 정상적으로 처리한다")
    void formatFeedDateTime_Success() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 20, 16, 20, 10);

        // when
        String formatted = DateUtils.formatFeedDateTime(dateTime);

        // then
        assertThat(formatted).isEqualTo("2023.08.20 16:20");
    }

    @Test
    @DisplayName("피드용 날짜가 null이면 빈 문자열을 반환한다")
    void formatFeedDateTime_WithNull_ReturnsEmpty() {
        // when
        String formatted = DateUtils.formatFeedDateTime(null);

        // then
        assertThat(formatted).isEmpty();
    }

    @Test
    @DisplayName("자정과 정오 시간을 포맷한다")
    void formatDateTime_EdgeTimes_Success() {
        // given
        LocalDateTime midnight = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        LocalDateTime noon = LocalDateTime.of(2023, 6, 15, 12, 0, 0);

        // when
        String formattedMidnight = DateUtils.formatLocalDateTime(midnight);
        String formattedNoon = DateUtils.formatLocalDateTime(noon);

        // then
        assertThat(formattedMidnight).isEqualTo("2023.01.01 00:00");
        assertThat(formattedNoon).isEqualTo("2023.06.15 12:00");
    }

    @Test
    @DisplayName("DateUtils 클래스는 인스턴스화할 수 없다")
    void constructor_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> {
            var constructor = DateUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(AssertionError.class);
    }

    @Test
    @DisplayName("상수 값이 올바르게 설정되어 있다")
    void constants_AreCorrect() {
        // then
        assertThat(DateUtils.NO_DATE_INFO).isEqualTo("정보 없음");
    }

    @Test
    @DisplayName("윤년 날짜를 올바르게 포맷한다")
    void formatDate_LeapYear_Success() {
        // given
        LocalDateTime leapYearDate = LocalDateTime.of(2024, 2, 29, 10, 30, 0);

        // when
        String formatted = DateUtils.formatDate(leapYearDate);

        // then
        assertThat(formatted).isEqualTo("2024-02-29");
    }
}
