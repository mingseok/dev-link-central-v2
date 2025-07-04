package dev.devlink.member.entity;

import dev.devlink.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Builder
    public Member(
            String name,
            String password,
            String email,
            String nickname
    ) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

    public static Member create(
            String name,
            String email,
            String nickname,
            String encodedPassword
    ) {
        return Member.builder()
                .name(name)
                .email(email)
                .nickname(nickname)
                .password(encodedPassword)
                .build();
    }
}
