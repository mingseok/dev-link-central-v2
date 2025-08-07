package dev.devlink.follow.repository;

import dev.devlink.follow.entity.Follow;
import dev.devlink.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowee(Member follower, Member followee);

    List<Follow> findAllByFollower(Member follower);

    List<Follow> findAllByFollowee(Member followee);
    
    boolean existsByFollowerAndFollowee(Member follower, Member followee);
    
    long countByFollowee(Member followee);
    
    long countByFollower(Member follower);
}
