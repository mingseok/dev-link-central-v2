package dev.devlink.profile.repository;

import dev.devlink.member.entity.Member;
import dev.devlink.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByMember(Member member);
    
    Optional<Profile> findByMemberId(Long memberId);
}
