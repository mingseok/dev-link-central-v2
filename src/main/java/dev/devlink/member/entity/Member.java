package dev.devlink.member.entity;

import dev.devlink.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private boolean deleted = Boolean.FALSE;

    @Builder
    public Member(String name, String passwordHash, String email, String nickname) {
        this.name = name;
        this.passwordHash = passwordHash;
        this.email = email;
        this.nickname = nickname;
    }
}
