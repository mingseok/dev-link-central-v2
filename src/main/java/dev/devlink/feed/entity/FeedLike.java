package dev.devlink.feed.entity;

import dev.devlink.common.BaseEntity;
import dev.devlink.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"feed_id", "member_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private FeedLike(Feed feed, Member member) {
        this.feed = feed;
        this.member = member;
    }

    public static FeedLike create(Feed feed, Member member) {
        return new FeedLike(feed, member);
    }
}
