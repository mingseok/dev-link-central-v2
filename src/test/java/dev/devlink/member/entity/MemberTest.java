package dev.devlink.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    @DisplayName("회원을 생성할 수 있다")
    void create_Success() {
        // when
        Member member = Member.create(
                "김민석",
                "hong@example.com",
                "김민석닉네임",
                "encodedPassword"
        );

        // then
        assertThat(member.getName()).isEqualTo("김민석");
        assertThat(member.getEmail()).isEqualTo("hong@example.com");
        assertThat(member.getNickname()).isEqualTo("김민석닉네임");
        assertThat(member.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("빌더 패턴으로 회원을 생성할 수 있다")
    void builder_Success() {
        // when
        Member member = Member.builder()
                .name("김선우")
                .email("kim@example.com")
                .nickname("김선우닉네임")
                .password("password123")
                .build();

        // then
        assertThat(member.getName()).isEqualTo("김선우");
        assertThat(member.getEmail()).isEqualTo("kim@example.com");
        assertThat(member.getNickname()).isEqualTo("김선우닉네임");
        assertThat(member.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("회원 정보가 모두 올바르게 설정된다")
    void memberInfo_SetCorrectly() {
        // when
        Member member = Member.create(
                "김현우",
                "lee@example.com",
                "영희닉네임",
                "$2a$10$encodedPassword"
        );

        // then
        assertThat(member.getName()).isEqualTo("김현우");
        assertThat(member.getEmail()).isEqualTo("lee@example.com");
        assertThat(member.getNickname()).isEqualTo("영희닉네임");
        assertThat(member.getPassword()).isEqualTo("$2a$10$encodedPassword");
    }
}
