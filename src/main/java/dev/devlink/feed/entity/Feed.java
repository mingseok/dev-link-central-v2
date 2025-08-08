package dev.devlink.feed.entity;

import dev.devlink.common.BaseEntity;
import dev.devlink.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
    public Feed(Member member, String content) {
        this.member = member;
        this.content = content;
    }

    public static Feed create(Member member, String content) {
        return Feed.builder()
                .member(member)
                .content(content)
                .build();
    }

    public Long getWriterId() {
        return member.getId();
    }

    public String getWriterNickname() {
        return member.getNickname();
    }

    public boolean isAuthor(Long memberId) {
        if (memberId == null) {
            return false;
        }
        return member.getId().equals(memberId);
    }
}
