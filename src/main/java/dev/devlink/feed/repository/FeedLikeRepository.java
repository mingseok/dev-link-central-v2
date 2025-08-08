package dev.devlink.feed.repository;

import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.entity.FeedLike;
import dev.devlink.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    long countByFeed(Feed feed);

    void deleteByFeedAndMember(Feed feed, Member member);

    boolean existsByFeedAndMember(Feed feed, Member member);
}
