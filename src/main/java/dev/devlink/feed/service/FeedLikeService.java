package dev.devlink.feed.service;

import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.entity.FeedLike;
import dev.devlink.feed.exception.FeedError;
import dev.devlink.feed.exception.FeedException;
import dev.devlink.feed.repository.FeedLikeRepository;
import dev.devlink.feed.repository.FeedRepository;
import dev.devlink.feed.service.dto.response.FeedLikeResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedLikeService {

    private final FeedLikeRepository feedLikeRepository;
    private final FeedRepository feedRepository;
    private final MemberService memberService;

    @Transactional
    public FeedLikeResponse likeOrCancel(Long memberId, Long feedId) {
        Member member = memberService.findMemberById(memberId);
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedError.NOT_FOUND));

        boolean isLiked = feedLikeRepository.existsByFeedAndMember(feed, member);
        if (isLiked) {
            return cancelLike(feed, member);
        }
        return addLike(feed, member);
    }

    private FeedLikeResponse cancelLike(Feed feed, Member member) {
        feedLikeRepository.deleteByFeedAndMember(feed, member);
        long likeCount = feedLikeRepository.countByFeed(feed);
        return FeedLikeResponse.from(false, likeCount);
    }

    private FeedLikeResponse addLike(Feed feed, Member member) {
        FeedLike feedLike = FeedLike.create(feed, member);
        feedLikeRepository.save(feedLike);
        long likeCount = feedLikeRepository.countByFeed(feed);
        return FeedLikeResponse.from(true, likeCount);
    }
}
