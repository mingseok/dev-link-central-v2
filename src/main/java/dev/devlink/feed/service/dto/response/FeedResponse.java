package dev.devlink.feed.service.dto.response;

import dev.devlink.common.utils.DateUtils;
import dev.devlink.feed.entity.Feed;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedResponse {

    private Long feedId;
    private Long writerId;
    private String writerNickname;
    private String content;
    private String createdAt;
    private boolean isMyFeed;
    private boolean isLiked;
    private long likeCount;

    public static FeedResponse from(
            Feed feed,
            Long currentUserId,
            boolean isLiked,
            long likeCount
    ) {
        return new FeedResponse(
                feed.getId(),
                feed.getWriterId(),
                feed.getWriterNickname(),
                feed.getContent(),
                DateUtils.formatFeedDateTime(feed.getCreatedAt()),
                feed.isAuthor(currentUserId),
                isLiked,
                likeCount
        );
    }
}
