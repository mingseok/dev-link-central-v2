package dev.devlink.comment.service;

import dev.devlink.feed.repository.FeedRepository;
import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.exception.FeedError;
import dev.devlink.feed.exception.FeedException;
import dev.devlink.comment.entity.FeedComment;
import dev.devlink.comment.exception.CommentError;
import dev.devlink.comment.exception.CommentException;
import dev.devlink.comment.repository.FeedCommentRepository;
import dev.devlink.comment.service.dto.request.CommentCreateRequest;
import dev.devlink.comment.service.dto.response.CommentResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeedCommentService implements CommentService {

    private final MemberService memberService;
    private final FeedRepository feedRepository;
    private final FeedCommentRepository commentRepository;

    @Override
    @Transactional
    public void save(CommentCreateRequest request, Long feedId, Long memberId) {
        Member member = memberService.findMemberById(memberId);
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedError.NOT_FOUND));

        validateParentComment(request.getParentId(), feedId);
        FeedComment comment = FeedComment.create(
                feed, member, request.getParentId(), request.getContent());

        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long feedId) {
        feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedError.NOT_FOUND));

        List<FeedComment> comments = commentRepository.findAllByFeedId(feedId);
        return buildCommentTree(comments);
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long memberId) {
        FeedComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentError.NOT_FOUND));
        
        comment.checkAuthor(memberId);
        if (commentRepository.existsByParentId(commentId)) {
            throw new CommentException(CommentError.HAS_CHILD_COMMENTS);
        }
        commentRepository.delete(comment);
    }

    private void validateParentComment(Long parentId, Long feedId) {
        if (parentId == null) {
            return;
        }
        FeedComment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentException(CommentError.PARENT_NOT_FOUND));
        
        if (!parent.getFeedId().equals(feedId)) {
            throw new CommentException(CommentError.PARENT_NOT_FOUND);
        }

        if (parent.getParentId() != null) {
            throw new CommentException(CommentError.REPLY_DEPTH_EXCEEDED);
        }
    }

    private List<CommentResponse> buildCommentTree(List<FeedComment> comments) {
        List<CommentResponse> rootComments = new ArrayList<>();
        Map<Long, CommentResponse> rootCommentMap = new HashMap<>();

        for (FeedComment comment : comments) {
            if (comment.getParentId() == null) {
                CommentResponse response = CommentResponse.from(comment);
                rootComments.add(response);
                rootCommentMap.put(comment.getId(), response);
            }
        }

        for (FeedComment comment : comments) {
            if (comment.getParentId() != null) {
                CommentResponse parent = rootCommentMap.get(comment.getParentId());
                if (parent != null) {
                    CommentResponse reply = CommentResponse.from(comment);
                    parent.addChild(reply);
                }
            }
        }
        
        return rootComments;
    }
}
