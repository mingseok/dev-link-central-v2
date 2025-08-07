package dev.devlink.follow.service.dto.response;

import dev.devlink.follow.constant.FollowConstants;
import dev.devlink.member.entity.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FollowResponse {

    private final Long memberId;
    private final String nickname;
    private final String joinedAt;
    private final Boolean isFollowing;

    public FollowResponse(Long memberId, String nickname, String joinedAt, Boolean isFollowing) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.joinedAt = joinedAt;
        this.isFollowing = isFollowing;
    }

    public static FollowResponse from(Member member) {
        return new FollowResponse(
                member.getId(),
                member.getNickname(),
                formatDateTime(member.getCreatedAt()),
                false
        );
    }

    public static FollowResponse from(Member member, boolean isFollowing) {
        return new FollowResponse(
                member.getId(),
                member.getNickname(),
                formatDateTime(member.getCreatedAt()),
                isFollowing
        );
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return FollowConstants.NO_DATE_INFO;
        }
        return dateTime.format(FollowConstants.DATE_FORMATTER);
    }
}
