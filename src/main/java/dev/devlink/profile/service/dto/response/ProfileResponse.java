package dev.devlink.profile.service.dto.response;

import dev.devlink.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {

    private Long memberId;
    private String nickname;
    private String joinedAt;
    private String bio;
    private boolean isFollowing;
    private long followersCount;
    private long followingsCount;

    public static ProfileResponse from(
            Member member,
            String bio,
            boolean isFollowing,
            long followers,
            long followings
    ) {
        return ProfileResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .joinedAt(member.getCreatedAt().toLocalDate().toString())
                .bio(bio)
                .isFollowing(isFollowing)
                .followersCount(followers)
                .followingsCount(followings)
                .build();
    }
}
