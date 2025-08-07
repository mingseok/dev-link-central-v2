package dev.devlink.follow.entity;

import dev.devlink.common.BaseEntity;
import dev.devlink.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"follower_id", "followee_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Member follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private Member followee;

    @Builder
    private Follow(Member follower, Member followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public static Follow create(Member follower, Member followee) {
        return Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();
    }
}
