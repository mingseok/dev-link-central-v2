package dev.devlink.profile.entity;

import dev.devlink.common.BaseEntity;
import dev.devlink.common.file.FileConstants;
import dev.devlink.member.entity.Member;
import dev.devlink.profile.constant.ProfileDefaults;
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

    @Column(name = "image_url")
    private String imageUrl;

    @Builder
    private Profile(Member member, String bio, String imageUrl) {
        this.member = member;
        this.bio = bio;
        this.imageUrl = imageUrl;
    }

    public static Profile create(Member member, String bio) {
        return Profile.builder()
                .member(member)
                .bio(bio)
                .imageUrl(FileConstants.DEFAULT_IMAGE_URL)
                .build();
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
