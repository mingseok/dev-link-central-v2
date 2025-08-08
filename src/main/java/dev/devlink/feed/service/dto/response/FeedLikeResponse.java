package dev.devlink.feed.service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedLikeResponse {

    private boolean isLiked;
    private long likeCount;

    public static FeedLikeResponse from(boolean isLiked, long likeCount) {
        return new FeedLikeResponse(isLiked, likeCount);
    }
}
