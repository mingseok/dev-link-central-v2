package dev.devlink.article.entity;

import dev.devlink.common.BaseEntity;
import dev.devlink.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public static Article create(Member member, String title, String content) {
        Article article = new Article();
        article.title = title;
        article.content = content;
        article.writer = member.getNickname();
        article.member = member;
        return article;
    }

    public Long getWriterId() {
        return member.getId();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
