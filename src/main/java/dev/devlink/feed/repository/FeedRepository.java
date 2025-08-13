package dev.devlink.feed.repository;

import dev.devlink.feed.entity.Feed;
import dev.devlink.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    @Query("SELECT f FROM Feed f WHERE f.member = :currentMember " +
           "OR f.member IN (SELECT fo.followee FROM Follow fo WHERE fo.follower = :currentMember) " +
           "ORDER BY f.createdAt DESC")
    List<Feed> findFeedsByFollowing(@Param("currentMember") Member currentMember);
}
