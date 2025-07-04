package dev.devlink.article.comment.entity;

import dev.devlink.article.comment.exception.CommentError;
import dev.devlink.article.comment.exception.CommentException;
import dev.devlink.article.entity.Article;
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
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "parent_id")
    private Long parentId;

    @Builder
    public Comment(
            Article article,
            Member member,
            String content,
            Long parentId
    ) {
        this.article = article;
        this.member = member;
        this.content = content;
        this.parentId = parentId;
    }

    public static Comment create(
            Article article,
            Member member,
            String content,
            Long parentId
    ) {
        return Comment.builder()
                .article(article)
                .member(member)
                .content(content)
                .parentId(parentId)
                .build();
    }

    public String getWriterNickname() {
        return member.getNickname();
    }

    public void checkAuthor(Long memberId) {
        if (!isAuthor(memberId)) {
            throw new CommentException(CommentError.NO_PERMISSION);
        }
    }

    private boolean isAuthor(Long memberId) {
        return this.member.getId().equals(memberId);
    }
}
