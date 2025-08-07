package dev.devlink.profile.entity;

import dev.devlink.common.BaseEntity;
import dev.devlink.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "bio")
    private String bio;

    @Builder
    private Profile(Member member, String bio) {
        this.member = member;
        this.bio = bio;
    }

    public static Profile create(Member member, String bio) {
        return Profile.builder()
                .member(member)
                .bio(bio)
                .build();
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }
}
