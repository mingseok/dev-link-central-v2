package dev.devlink.profile.service.dto.response;

import dev.devlink.common.utils.DateUtils;
import dev.devlink.member.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProfileResponse {

    private Long memberId;
    private String nickname;
    private String joinedAt;
    private String bio;
    private String imageUrl;
    private Boolean isFollowing;
    private Long followersCount;
    private Long followingsCount;

    public static ProfileResponse from(
            Member member,
            String bio,
            String imageUrl,
            boolean isFollowing,
            long followers,
            long followings
    ) {
        return new ProfileResponse(
                member.getId(),
                member.getNickname(),
                DateUtils.formatDate(member.getCreatedAt()),
                bio,
                imageUrl,
                isFollowing,
                followers,
                followings
        );
    }
}
