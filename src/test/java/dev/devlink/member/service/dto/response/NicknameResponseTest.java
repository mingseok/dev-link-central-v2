package dev.devlink.member.service.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NicknameResponseTest {

    @Test
    @DisplayName("생성자로 닉네임 응답을 생성할 수 있다")
    void constructor_CreatesNicknameResponse() {
        // given
        String nickname = "김민석닉네임";

        // when
        NicknameResponse response = new NicknameResponse(nickname);

        // then
        assertThat(response.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("정적 팩토리 메서드로 응답을 생성할 수 있다")
    void from_CreatesNicknameResponseFromString() {
        // given
        String nickname = "김선우닉네임";

        // when
        NicknameResponse response = NicknameResponse.from(nickname);

        // then
        assertThat(response.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("getter가 올바르게 동작한다")
    void getter_WorksCorrectly() {
        // given
        NicknameResponse response = new NicknameResponse("테스트닉네임");

        // when & then
        assertThat(response.getNickname()).isEqualTo("테스트닉네임");
    }

    @Test
    @DisplayName("null 닉네임으로도 응답을 생성할 수 있다")
    void nullNickname_CanCreateResponse() {
        // given
        String nickname = null;

        // when
        NicknameResponse response = new NicknameResponse(nickname);

        // then
        assertThat(response.getNickname()).isNull();
    }

    @Test
    @DisplayName("빈 문자열 닉네임으로도 응답을 생성할 수 있다")
    void emptyNickname_CanCreateResponse() {
        // given
        String nickname = "";

        // when
        NicknameResponse response = NicknameResponse.from(nickname);

        // then
        assertThat(response.getNickname()).isEmpty();
    }
}
