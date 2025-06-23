package dev.devlink.member.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponse {

    private Long id;

    public static SignUpResponse from(Long memberId) {
        return new SignUpResponse(memberId);
    }
}
