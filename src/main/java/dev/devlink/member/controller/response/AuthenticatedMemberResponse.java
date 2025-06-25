package dev.devlink.member.controller.response;

import dev.devlink.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedMemberResponse {

    private final Long id;
    private final String nickname;

    public static AuthenticatedMemberResponse from(Member member) {
        return new AuthenticatedMemberResponse(member.getId(), member.getNickname());
    }
}
