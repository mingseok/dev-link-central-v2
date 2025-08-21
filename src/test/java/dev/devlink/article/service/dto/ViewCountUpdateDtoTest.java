package dev.devlink.article.service.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ViewCountUpdateDtoTest {

    @Test
    @DisplayName("DTO를 올바르게 생성한다")
    void create_Success() {
        // given
        Long articleId = 123L;
        Long viewCount = 45L;

        // when
        ViewCountUpdateDto dto = new ViewCountUpdateDto(articleId, viewCount);

        // then
        assertThat(dto.getArticleId()).isEqualTo(123L);
        assertThat(dto.getViewCount()).isEqualTo(45L);
    }

    @Test
    @DisplayName("0값도 올바르게 처리한다")
    void create_WithZeroValues_Success() {
        // given
        Long articleId = 0L;
        Long viewCount = 0L;

        // when
        ViewCountUpdateDto dto = new ViewCountUpdateDto(articleId, viewCount);

        // then
        assertThat(dto.getArticleId()).isEqualTo(0L);
        assertThat(dto.getViewCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("큰 숫자도 올바르게 처리한다")
    void create_WithLargeValues_Success() {
        // given
        Long articleId = 999999999L;
        Long viewCount = 888888888L;

        // when
        ViewCountUpdateDto dto = new ViewCountUpdateDto(articleId, viewCount);

        // then
        assertThat(dto.getArticleId()).isEqualTo(999999999L);
        assertThat(dto.getViewCount()).isEqualTo(888888888L);
    }

    @Test
    @DisplayName("null 값도 처리할 수 있다")
    void create_WithNullValues_Success() {
        // when
        ViewCountUpdateDto dto = new ViewCountUpdateDto(null, null);

        // then
        assertThat(dto.getArticleId()).isNull();
        assertThat(dto.getViewCount()).isNull();
    }
}
