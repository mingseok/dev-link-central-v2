package dev.devlink.follow.service.dto.response;

import dev.devlink.common.utils.DateUtils;
import dev.devlink.member.entity.Member;
import lombok.Getter;

@Getter
public class FollowResponse {

    private final Long memberId;
    private final String nickname;
    private final String joinedAt;
    private final Boolean isFollowing;

    public FollowResponse(
            Long memberId,
            String nickname,
            String joinedAt,
            Boolean isFollowing
    ) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.joinedAt = joinedAt;
        this.isFollowing = isFollowing;
    }

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
