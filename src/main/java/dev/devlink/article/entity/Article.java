package dev.devlink.article.entity;

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
public class Article extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "writer", nullable = false)
    private String writer;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
    public Article(Member member, String writer, String title, String content) {
        this.member = member;
        this.writer = writer;
        this.title = title;
        this.content = content;
    }

    public static Article create(Member member, String title, String content) {
        return Article.builder()
                .member(member)
                .writer(member.getNickname())
                .title(title)
                .content(content)
                .build();
    }

    public Long getWriterId() {
        return member.getId();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
