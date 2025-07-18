package dev.devlink.member.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameResponse {

    private final String nickname;

    public static NicknameResponse from(String nickname) {
        return new NicknameResponse(nickname);
    }
}
