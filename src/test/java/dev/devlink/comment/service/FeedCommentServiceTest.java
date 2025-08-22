package dev.devlink.comment.service;

import dev.devlink.comment.entity.FeedComment;
import dev.devlink.comment.exception.CommentError;
import dev.devlink.comment.exception.CommentException;
import dev.devlink.comment.repository.FeedCommentRepository;
import dev.devlink.comment.service.dto.request.CommentCreateRequest;
import dev.devlink.comment.service.dto.response.CommentResponse;
import dev.devlink.feed.entity.Feed;
import dev.devlink.feed.exception.FeedError;
import dev.devlink.feed.exception.FeedException;
import dev.devlink.feed.repository.FeedRepository;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FeedCommentServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private FeedCommentRepository commentRepository;

    @InjectMocks
    private FeedCommentService feedCommentService;

    private Member member;
    private Feed feed;
    private CommentCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        member = Member.create("testName", "test@example.com", "testNickname", "password123");
        ReflectionTestUtils.setField(member, "id", 1L);
        
        feed = Feed.create(member, "Test feed content", null);
        ReflectionTestUtils.setField(feed, "id", 1L);

        createRequest = new CommentCreateRequest();
        ReflectionTestUtils.setField(createRequest, "content", "테스트 댓글입니다.");
        ReflectionTestUtils.setField(createRequest, "parentId", null);
    }

    @Test
    @DisplayName("댓글을 정상적으로 저장한다")
    void save_Success() {
        // given
        Long feedId = 1L;
        Long memberId = 1L;

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));

        // when
        feedCommentService.save(createRequest, feedId, memberId);

        // then
        verify(memberService).findMemberById(memberId);
        verify(feedRepository).findById(feedId);
        verify(commentRepository).save(any(FeedComment.class));
    }

    @Test
    @DisplayName("존재하지 않는 피드에 댓글 저장 시 예외가 발생한다")
    void save_FeedNotFound() {
        // given
        Long feedId = 999L;
        Long memberId = 1L;

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(feedRepository.findById(feedId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedCommentService.save(createRequest, feedId, memberId))
                .isInstanceOf(FeedException.class)
                .hasMessage(FeedError.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("대댓글을 정상적으로 저장한다")
    void save_ReplyComment_Success() {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        Long parentId = 2L;

        FeedComment parentComment = FeedComment.create(feed, member, null, "부모 댓글");
        ReflectionTestUtils.setField(parentComment, "id", parentId);
        ReflectionTestUtils.setField(createRequest, "parentId", parentId);

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));
        given(commentRepository.findById(parentId)).willReturn(Optional.of(parentComment));

        // when
        feedCommentService.save(createRequest, feedId, memberId);

        // then
        verify(commentRepository).save(any(FeedComment.class));
    }

    @Test
    @DisplayName("존재하지 않는 부모 댓글 ID로 대댓글 저장 시 예외가 발생한다")
    void save_ParentCommentNotFound() {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        Long parentId = 999L;

        ReflectionTestUtils.setField(createRequest, "parentId", parentId);

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));
        given(commentRepository.findById(parentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedCommentService.save(createRequest, feedId, memberId))
                .isInstanceOf(CommentException.class)
                .hasMessage(CommentError.PARENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("다른 피드의 댓글을 부모로 하는 대댓글 저장 시 예외가 발생한다")
    void save_ParentCommentFromDifferentFeed() {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        Long parentId = 2L;

        Feed differentFeed = Feed.create(member, "다른 피드", null);
        ReflectionTestUtils.setField(differentFeed, "id", 2L);
        
        FeedComment parentComment = FeedComment.create(differentFeed, member, null, "부모 댓글");
        ReflectionTestUtils.setField(parentComment, "id", parentId);
        ReflectionTestUtils.setField(createRequest, "parentId", parentId);

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));
        given(commentRepository.findById(parentId)).willReturn(Optional.of(parentComment));

        // when & then
        assertThatThrownBy(() -> feedCommentService.save(createRequest, feedId, memberId))
                .isInstanceOf(CommentException.class)
                .hasMessage(CommentError.PARENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("대댓글에 대댓글을 달려고 하면 예외가 발생한다")
    void save_ReplyDepthExceeded() {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        Long parentId = 2L;

        FeedComment parentComment = FeedComment.create(feed, member, 1L, "대댓글"); // parentId가 있는 댓글
        ReflectionTestUtils.setField(parentComment, "id", parentId);
        ReflectionTestUtils.setField(createRequest, "parentId", parentId);

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));
        given(commentRepository.findById(parentId)).willReturn(Optional.of(parentComment));

        // when & then
        assertThatThrownBy(() -> feedCommentService.save(createRequest, feedId, memberId))
                .isInstanceOf(CommentException.class)
                .hasMessage(CommentError.REPLY_DEPTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("피드의 댓글 목록을 정상적으로 조회한다")
    void getComments_Success() {
        // given
        Long feedId = 1L;

        FeedComment parentComment = FeedComment.create(feed, member, null, "부모 댓글");
        ReflectionTestUtils.setField(parentComment, "id", 1L);
        
        FeedComment childComment = FeedComment.create(feed, member, 1L, "자식 댓글");
        ReflectionTestUtils.setField(childComment, "id", 2L);

        List<FeedComment> comments = Arrays.asList(parentComment, childComment);

        given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));
        given(commentRepository.findAllByFeedId(feedId)).willReturn(comments);

        // when
        List<CommentResponse> result = feedCommentService.getComments(feedId);

        // then
        assertThat(result).hasSize(1); // 부모 댓글만 루트로 반환
        assertThat(result.get(0).getChildren()).hasSize(1); // 자식 댓글 1개
    }

    @Test
    @DisplayName("존재하지 않는 피드의 댓글 조회 시 예외가 발생한다")
    void getComments_FeedNotFound() {
        // given
        Long feedId = 999L;

        given(feedRepository.findById(feedId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedCommentService.getComments(feedId))
                .isInstanceOf(FeedException.class)
                .hasMessage(FeedError.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("댓글을 정상적으로 삭제한다")
    void delete_Success() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        FeedComment comment = FeedComment.create(feed, member, null, "삭제할 댓글");
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentRepository.existsByParentId(commentId)).willReturn(false);

        // when
        feedCommentService.delete(commentId, memberId);

        // then
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제 시 예외가 발생한다")
    void delete_CommentNotFound() {
        // given
        Long commentId = 999L;
        Long memberId = 1L;

        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedCommentService.delete(commentId, memberId))
                .isInstanceOf(CommentException.class)
                .hasMessage(CommentError.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("자식 댓글이 있는 댓글 삭제 시 예외가 발생한다")
    void delete_HasChildComments() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        FeedComment comment = FeedComment.create(feed, member, null, "부모 댓글");
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentRepository.existsByParentId(commentId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> feedCommentService.delete(commentId, memberId))
                .isInstanceOf(CommentException.class)
                .hasMessage(CommentError.HAS_CHILD_COMMENTS.getMessage());
    }

    @Test
    @DisplayName("작성자가 아닌 사용자가 댓글 삭제 시 예외가 발생한다")
    void delete_NoPermission() {
        // given
        Long commentId = 1L;
        Long memberId = 999L; // 다른 사용자

        FeedComment comment = FeedComment.create(feed, member, null, "다른 사용자의 댓글");
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> feedCommentService.delete(commentId, memberId))
                .isInstanceOf(CommentException.class)
                .hasMessage(CommentError.NO_PERMISSION.getMessage());
    }
}
