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

class SignUpRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 회원가입 요청이 검증을 통과한다")
    void validSignUpRequest_PassesValidation() {
        // given
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "password123",
                "hong@example.com",
                "김민석닉네임"
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("이름이 비어있으면 검증에 실패한다")
    void blankName_FailsValidation() {
        // given
        SignUpRequest request = new SignUpRequest(
                "",
                "password123",
                "hong@example.com",
                "김민석닉네임"
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("이름을 입력해주세요");
    }

    @Test
    @DisplayName("이름이 50자를 초과하면 검증에 실패한다")
    void nameTooLong_FailsValidation() {
        // given
        String longName = "가".repeat(51);
        SignUpRequest request = new SignUpRequest(
                longName,
                "password123",
                "hong@example.com",
                "김민석닉네임"
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("이름은 50자 이내로 입력해 주세요");
    }

    @Test
    @DisplayName("비밀번호가 비어있으면 검증에 실패한다")
    void blankPassword_FailsValidation() {
        // given
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "",
                "hong@example.com",
                "김민석닉네임"
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("비밀번호를 입력해주세요"))).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 4자 미만이면 검증에 실패한다")
    void passwordTooShort_FailsValidation() {
        // given
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "ab1",
                "hong@example.com",
                "김민석닉네임"
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("비밀번호는 4~20자리로 입력해 주세요"))).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 20자를 초과하면 검증에 실패한다")
    void passwordTooLong_FailsValidation() {
        // given
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "a1234567890123456789a",
                "hong@example.com",
                "김민석닉네임"
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("비밀번호는 4~20자리로 입력해 주세요"))).isTrue();
    }

    @Test
    @DisplayName("비밀번호에 영문자가 없으면 검증에 실패한다")
    void passwordWithoutLetter_FailsValidation() {
        // given
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "123456",
                "hong@example.com",
                "김민석닉네임"
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("비밀번호는 최소 하나의 영문자와 숫자를 포함해야 합니다"))).isTrue();
    }

    @Test
    @DisplayName("비밀번호에 숫자가 없으면 검증에 실패한다")
    void passwordWithoutDigit_FailsValidation() {
        // given
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "abcdef",
                "hong@example.com",
                "김민석닉네임"
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("비밀번호는 최소 하나의 영문자와 숫자를 포함해야 합니다"))).isTrue();
    }

    @Test
    @DisplayName("이메일이 비어있으면 검증에 실패한다")
    void blankEmail_FailsValidation() {
        // given
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "password123",
                "",
                "김민석닉네임"
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
        assertThat(violations.stream().anyMatch(v -> 
                v.getMessage().contains("이메일을 입력해주세요"))).isTrue();
    }

    @Test
    @DisplayName("이메일 형식이 잘못되면 검증에 실패한다")
    void invalidEmailFormat_FailsValidation() {
        // given
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "password123",
                "invalid-email",
                "김민석닉네임"
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("닉네임이 비어있으면 검증에 실패한다")
    void blankNickname_FailsValidation() {
        // given
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "password123",
                "hong@example.com",
                ""
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("닉네임을 입력해주세요");
    }

    @Test
    @DisplayName("닉네임이 100자를 초과하면 검증에 실패한다")
    void nicknameTooLong_FailsValidation() {
        // given
        String longNickname = "가".repeat(101);
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "password123",
                "hong@example.com",
                longNickname
        );

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("닉네임은 100자 이내로 입력해 주세요");
    }
}
