package dev.devlink.article.entity;

import dev.devlink.article.exception.ArticleError;
import dev.devlink.article.exception.ArticleException;
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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Builder
    public Article(Member member, String title, String content) {
        this.member = member;
        this.title = title;
        this.content = content;
    }

    public static Article create(Member member, String title, String content) {
        return Article.builder()
                .member(member)
                .title(title)
                .content(content)
                .build();
    }

    public Long getWriterId() {
        return member.getId();
    }

    public String getWriterNickname() {
        return member.getNickname();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public boolean isAuthor(Long memberId) {
        if (memberId == null) {
            return false;
        }

        Long writerId = member.getId();
        return writerId.equals(memberId);
    }

    public void checkAuthor(Long memberId) {
        if (!isAuthor(memberId)) {
            throw new ArticleException(ArticleError.NO_PERMISSION);
        }
    }

    public void addViewCount(Long value) {
        this.viewCount += value;
    }
}
