package dev.devlink.feed.service;

import dev.devlink.comment.repository.FeedCommentRepository;
import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.exception.FeedError;
import dev.devlink.feed.exception.FeedException;
import dev.devlink.feed.repository.FeedLikeRepository;
import dev.devlink.feed.repository.FeedRepository;
import dev.devlink.feed.service.dto.request.FeedCreateRequest;
import dev.devlink.feed.service.dto.response.FeedResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedCommentRepository commentRepository;
    private final MemberService memberService;

    @Transactional
    public void createFeed(Long memberId, FeedCreateRequest request) {
        Member member = memberService.findMemberById(memberId);
        Feed feed = Feed.create(member, request.getContent());
        feedRepository.save(feed);
    }

    @Transactional(readOnly = true)
    public List<FeedResponse> getFeeds(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        List<Feed> feeds = feedRepository.findFeedsByFollowing(member);
        
        List<FeedResponse> result = new ArrayList<>();
        for (Feed feed : feeds) {
            boolean isLiked = feedLikeRepository.existsByFeedAndMember(feed, member);
            long likeCount = feedLikeRepository.countByFeed(feed);
            long commentCount = commentRepository.countByFeedId(feed.getId());
            FeedResponse response = FeedResponse.from(feed, memberId, isLiked, likeCount, commentCount);
            result.add(response);
        }
        
        return result;
    }

    @Transactional
    public void deleteFeed(Long memberId, Long feedId) {
        Feed feed = findFeedById(feedId);

        if (!feed.isAuthor(memberId)) {
            throw new FeedException(FeedError.NO_PERMISSION);
        }
        feedRepository.delete(feed);
    }

    public Feed findFeedById(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedError.NOT_FOUND));
    }
}
