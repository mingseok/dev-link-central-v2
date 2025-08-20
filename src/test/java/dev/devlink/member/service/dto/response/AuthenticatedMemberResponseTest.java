package dev.devlink.member.service.dto.response;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticatedMemberResponseTest {

    @Test
    @DisplayName("생성자로 인증된 회원 응답을 생성할 수 있다")
    void constructor_CreatesAuthenticatedMemberResponse() {
        // given
        Long id = 1L;
        String nickname = "김민석닉네임";

        // when
        AuthenticatedMemberResponse response = new AuthenticatedMemberResponse(id, nickname);

        // then
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("Member 엔티티로부터 응답을 생성할 수 있다")
    void from_CreatesResponseFromMember() {
        // given
        Member member = Member.builder()
                .name("김민석")
                .email("hong@example.com")
                .nickname("김민석닉네임")
                .password("password123")
                .build();

        // when
        AuthenticatedMemberResponse response = AuthenticatedMemberResponse.from(member);

        // then
        assertThat(response.getId()).isNull();
        assertThat(response.getNickname()).isEqualTo("김민석닉네임");
    }

    @Test
    @DisplayName("getter가 올바르게 동작한다")
    void getters_WorkCorrectly() {
        // given
        AuthenticatedMemberResponse response = new AuthenticatedMemberResponse(
                999L,
                "테스트닉네임"
        );

        // when & then
        assertThat(response.getId()).isEqualTo(999L);
        assertThat(response.getNickname()).isEqualTo("테스트닉네임");
    }

    @Test
    @DisplayName("ID가 null인 Member로부터 응답을 생성할 수 있다")
    void from_WithNullId_CreatesResponse() {
        // given
        Member member = Member.builder()
                .name("김민석")
                .email("hong@example.com")
                .nickname("김민석닉네임")
                .password("password123")
                .build();

        // when
        AuthenticatedMemberResponse response = AuthenticatedMemberResponse.from(member);

        // then
        assertThat(response.getId()).isNull();
        assertThat(response.getNickname()).isEqualTo("김민석닉네임");
    }
}
