package dev.devlink.article.comment.entity;

import dev.devlink.article.entity.Article;
import dev.devlink.common.BaseEntity;
import dev.devlink.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    public static Comment create(
            Article article,
            Member member,
            String content,
            Comment parent
    ) {
        Comment comment = new Comment();
        comment.article = article;
        comment.member = member;
        comment.content = content;
        comment.parent = parent;
        return comment;
    }

    public Long getWriterId() {
        return this.member.getId();
    }

    public Long getParentIdOrNull() {
        if (parent != null) {
            return parent.getId();
        }
        return null;
    }
}
