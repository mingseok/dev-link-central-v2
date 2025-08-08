package dev.devlink.comment.service;

import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.service.FeedService;
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
    private final FeedService feedService;
    private final FeedCommentRepository CommentRepository;

    @Override
    @Transactional
    public void save(CommentCreateRequest request, Long feedId, Long memberId) {
        Member member = memberService.findMemberById(memberId);
        Feed feed = feedService.findFeedById(feedId);
        
        validateParentComment(request.getParentId());
        FeedComment comment = FeedComment.create(
                feed, member, request.getParentId(), request.getContent());

        CommentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long feedId) {
        feedService.findFeedById(feedId);
        List<FeedComment> comments = CommentRepository.findAllByFeedId(feedId);
        return buildCommentTree(comments);
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long memberId) {
        FeedComment comment = CommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentError.NOT_FOUND));
        
        comment.checkAuthor(memberId);
        if (CommentRepository.existsByParentId(commentId)) {
            throw new CommentException(CommentError.HAS_CHILD_COMMENTS);
        }
        CommentRepository.delete(comment);
    }

    private void validateParentComment(Long parentId) {
        if (parentId == null) {
            return;
        }

        boolean parentExists = CommentRepository.existsById(parentId);
        if (!parentExists) {
            throw new CommentException(CommentError.PARENT_NOT_FOUND);
        }
    }

    private List<CommentResponse> buildCommentTree(List<FeedComment> comments) {
        List<CommentResponse> rootComments = new ArrayList<>();
        Map<Long, CommentResponse> responseMap = new HashMap<>();

        for (FeedComment comment : comments) {
            CommentResponse response = CommentResponse.from(comment);
            responseMap.put(comment.getId(), response);
        }

        for (FeedComment comment : comments) {
            CommentResponse response = responseMap.get(comment.getId());
            Long parentId = comment.getParentId();
            
            if (parentId == null) {
                rootComments.add(response);
            } else {
                CommentResponse parent = responseMap.get(parentId);
                if (parent != null) {
                    parent.addChild(response);
                }
            }
        }
        
        return rootComments;
    }
}
