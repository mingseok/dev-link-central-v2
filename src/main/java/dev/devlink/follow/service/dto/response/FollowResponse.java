package dev.devlink.follow.service.dto.response;

import dev.devlink.common.utils.DateUtils;
import dev.devlink.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponse {

    private final Long memberId;
    private final String nickname;
    private final String joinedAt;
    private final Boolean isFollowing;

    public static FollowResponse from(Member member) {
        return new FollowResponse(
                member.getId(),
                member.getNickname(),
                DateUtils.formatDate(member.getCreatedAt()),
                false
        );
    }

    public static FollowResponse from(Member member, boolean isFollowing) {
        return new FollowResponse(
                member.getId(),
                member.getNickname(),
                DateUtils.formatDate(member.getCreatedAt()),
                isFollowing
        );
    }
}
