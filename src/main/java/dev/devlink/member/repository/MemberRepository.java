package dev.devlink.member.repository;

import dev.devlink.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByIdAndDeletedFalse(Long id);

    Optional<Member> findByEmailAndDeletedFalse(String email);

    @Query("SELECT m.nickname FROM Member m WHERE m.id = :memberId AND m.deleted = false")
    Optional<String> findNicknameById(@Param("memberId") Long memberId);
}
