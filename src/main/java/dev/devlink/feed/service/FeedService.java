package dev.devlink.feed.service;

import dev.devlink.comment.repository.FeedCommentRepository;
import dev.devlink.file.service.FileUploadService;
import dev.devlink.file.constant.FileConstants;
import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.exception.FeedError;
import dev.devlink.feed.exception.FeedException;
import dev.devlink.feed.repository.FeedLikeRepository;
import dev.devlink.feed.repository.FeedRepository;
import dev.devlink.feed.service.dto.response.FeedResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import dev.devlink.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final MemberService memberService;
    private final FileUploadService fileUploadService;
    private final ProfileService profileService;

    @Transactional
    public void createFeed(Long memberId, String content, MultipartFile file) {
        Member member = memberService.findMemberById(memberId);
        String imageUrl = null;

        if (file != null && !file.isEmpty()) {
            imageUrl = fileUploadService.uploadFile(file, FileConstants.FEED);
        }
        Feed feed = Feed.create(member, content, imageUrl);
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
            long commentCount = feedCommentRepository.countByFeedId(feed.getId());
            String profileImageUrl = profileService.getProfileImageUrl(feed.getWriterId());
            
            FeedResponse response = FeedResponse.from(
                    feed, memberId, profileImageUrl, isLiked, likeCount, commentCount);

            result.add(response);
        }

        return result;
    }
    
    @Transactional
    public void deleteFeed(Long memberId, Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedError.NOT_FOUND));

        if (!feed.isAuthor(memberId)) {
            throw new FeedException(FeedError.NO_PERMISSION);
        }
        feedRepository.delete(feed);
    }
}
