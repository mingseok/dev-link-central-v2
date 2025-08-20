package dev.devlink.member.service.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SignInRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 로그인 요청이 검증을 통과한다")
    void validSignInRequest_PassesValidation() {
        // given
        SignInRequest request = new SignInRequest(
                "hong@example.com",
                "password123"
        );

        // when
        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("이메일이 비어있으면 검증에 실패한다")
    void blankEmail_FailsValidation() {
        // given
        SignInRequest request = new SignInRequest(
                "",
                "password123"
        );

        // when
        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("이메일을 입력해주세요"))).isTrue();
    }

    @Test
    @DisplayName("이메일 형식이 잘못되면 검증에 실패한다")
    void invalidEmailFormat_FailsValidation() {
        // given
        SignInRequest request = new SignInRequest(
                "invalid-email",
                "password123"
        );

        // when
        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("비밀번호가 비어있으면 검증에 실패한다")
    void blankPassword_FailsValidation() {
        // given
        SignInRequest request = new SignInRequest(
                "hong@example.com",
                ""
        );

        // when
        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("비밀번호를 입력해주세요"))).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 4자 미만이면 검증에 실패한다")
    void passwordTooShort_FailsValidation() {
        // given
        SignInRequest request = new SignInRequest(
                "hong@example.com",
                "ab1"
        );

        // when
        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("비밀번호는 4~20자리로 입력해 주세요"))).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 20자를 초과하면 검증에 실패한다")
    void passwordTooLong_FailsValidation() {
        // given
        SignInRequest request = new SignInRequest(
                "hong@example.com",
                "a1234567890123456789a"
        );

        // when
        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("비밀번호는 4~20자리로 입력해 주세요"))).isTrue();
    }

    @Test
    @DisplayName("비밀번호에 영문자가 없으면 검증에 실패한다")
    void passwordWithoutLetter_FailsValidation() {
        // given
        SignInRequest request = new SignInRequest(
                "hong@example.com",
                "123456"
        );

        // when
        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("비밀번호는 최소 하나의 영문자와 숫자를 포함해야 합니다"))).isTrue();
    }

    @Test
    @DisplayName("비밀번호에 숫자가 없으면 검증에 실패한다")
    void passwordWithoutDigit_FailsValidation() {
        // given
        SignInRequest request = new SignInRequest(
                "hong@example.com",
                "abcdef"
        );

        // when
        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("비밀번호는 최소 하나의 영문자와 숫자를 포함해야 합니다"))).isTrue();
    }

    @Test
    @DisplayName("getter가 올바르게 동작한다")
    void getters_WorkCorrectly() {
        // given
        SignInRequest request = new SignInRequest(
                "hong@example.com",
                "password123"
        );

        // when & then
        assertThat(request.getEmail()).isEqualTo("hong@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
    }
}
