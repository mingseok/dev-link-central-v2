package dev.devlink.article.service.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleCreateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("유효한 데이터로 생성할 수 있다")
    void create_ValidData_Success() {
        // given
        ArticleCreateRequest request = new ArticleCreateRequest("유효한 제목입니다", "유효한 내용입니다.");

        // when
        Set<ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
        assertThat(request.getTitle()).isEqualTo("유효한 제목입니다");
        assertThat(request.getContent()).isEqualTo("유효한 내용입니다.");
    }

    @Test
    @DisplayName("제목이 null이면 검증에 실패한다")
    void create_NullTitle_ValidationFailed() {
        // given
        ArticleCreateRequest request = new ArticleCreateRequest(null, "유효한 내용입니다.");

        // when
        Set<ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("게시글 제목은 필수 입력 항목입니다.");
    }

    @Test
    @DisplayName("제목이 빈 문자열이면 검증에 실패한다")
    void create_EmptyTitle_ValidationFailed() {
        // given
        ArticleCreateRequest request = new ArticleCreateRequest("", "유효한 내용입니다.");

        // when
        Set<ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        boolean hasBlankMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("게시글 제목은 필수 입력 항목입니다."));
        assertThat(hasBlankMessage).isTrue();
    }

    @Test
    @DisplayName("제목이 너무 짧으면 검증에 실패한다")
    void create_TitleTooShort_ValidationFailed() {
        // given
        ArticleCreateRequest request = new ArticleCreateRequest("짧", "유효한 내용입니다.");

        // when
        Set<ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("게시글 제목은 3자 이상 100자 이하이어야 합니다.");
    }

    @Test
    @DisplayName("제목이 너무 길면 검증에 실패한다")
    void create_TitleTooLong_ValidationFailed() {
        // given
        String longTitle = "a".repeat(101);
        ArticleCreateRequest request = new ArticleCreateRequest(longTitle, "유효한 내용입니다.");

        // when
        Set<ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("게시글 제목은 3자 이상 100자 이하이어야 합니다.");
    }

    @Test
    @DisplayName("내용이 null이면 검증에 실패한다")
    void create_NullContent_ValidationFailed() {
        // given
        ArticleCreateRequest request = new ArticleCreateRequest("유효한 제목입니다", null);

        // when
        Set<ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("게시글 내용은 필수 입력 항목입니다.");
    }

    @Test
    @DisplayName("내용이 빈 문자열이면 검증에 실패한다")
    void create_EmptyContent_ValidationFailed() {
        // given
        ArticleCreateRequest request = new ArticleCreateRequest("유효한 제목입니다", "");

        // when
        Set<ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        boolean hasBlankMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("게시글 내용은 필수 입력 항목입니다."));
        assertThat(hasBlankMessage).isTrue();
    }

    @Test
    @DisplayName("내용이 너무 짧으면 검증에 실패한다")
    void create_ContentTooShort_ValidationFailed() {
        // given
        ArticleCreateRequest request = new ArticleCreateRequest("유효한 제목입니다", "짧");

        // when
        Set<ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("게시글 내용은 3자 이상 10000자 이하이어야 합니다.");
    }

    @Test
    @DisplayName("내용이 너무 길면 검증에 실패한다")
    void create_ContentTooLong_ValidationFailed() {
        // given
        String longContent = "a".repeat(10001);
        ArticleCreateRequest request = new ArticleCreateRequest("유효한 제목입니다", longContent);

        // when
        Set<ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("게시글 내용은 3자 이상 10000자 이하이어야 합니다.");
    }
}
