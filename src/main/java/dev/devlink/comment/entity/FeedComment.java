package dev.devlink.comment.entity;

import dev.devlink.comment.exception.CommentError;
import dev.devlink.comment.exception.CommentException;
import dev.devlink.common.BaseEntity;
import dev.devlink.feed.entity.Feed;
import dev.devlink.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "parent_id")
    private Long parentId;

    @Builder
    public FeedComment(
            Feed feed,
            Member member,
            String content,
            Long parentId
    ) {
        this.feed = feed;
        this.member = member;
        this.content = content;
        this.parentId = parentId;
    }

    public static FeedComment create(
            Feed feed,
            Member member,
            Long parentId,
            String content
    ) {
        return FeedComment.builder()
                .feed(feed)
                .member(member)
                .parentId(parentId)
                .content(content)
                .build();
    }

    public String getWriterNickname() {
        return member.getNickname();
    }

    public Member getWriter() {
        return this.member;
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
