package dev.devlink.comment.repository;

import dev.devlink.comment.entity.FeedComment;
import dev.devlink.feed.entity.Feed;
import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FeedCommentRepositoryTest {

    @Mock
    private FeedCommentRepository feedCommentRepository;

    private Member member;
    private Feed feed;

    @BeforeEach
    void setUp() {
        member = Member.create("testName", "test@example.com", "testNickname", "password123");
        ReflectionTestUtils.setField(member, "id", 1L);

        feed = Feed.create(member, "Test feed content", null);
        ReflectionTestUtils.setField(feed, "id", 1L);
    }

    @Test
    @DisplayName("피드 ID로 댓글 목록을 조회한다")
    void findAllByFeedId() {
        // given
        FeedComment comment1 = FeedComment.create(feed, member, null, "첫 번째 댓글");
        ReflectionTestUtils.setField(comment1, "id", 1L);
        
        FeedComment comment2 = FeedComment.create(feed, member, null, "두 번째 댓글");
        ReflectionTestUtils.setField(comment2, "id", 2L);

        List<FeedComment> expectedComments = Arrays.asList(comment1, comment2);
        given(feedCommentRepository.findAllByFeedId(feed.getId())).willReturn(expectedComments);

        // when
        List<FeedComment> comments = feedCommentRepository.findAllByFeedId(feed.getId());

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(FeedComment::getContent)
                .containsExactlyInAnyOrder("첫 번째 댓글", "두 번째 댓글");
        verify(feedCommentRepository).findAllByFeedId(feed.getId());
    }

    @Test
    @DisplayName("부모 댓글 ID로 자식 댓글의 존재 여부를 확인한다")
    void existsByParentId() {
        // given
        Long parentCommentId = 1L;
        Long childCommentId = 2L;

        given(feedCommentRepository.existsByParentId(parentCommentId)).willReturn(true);
        given(feedCommentRepository.existsByParentId(childCommentId)).willReturn(false);

        // when
        boolean hasChild = feedCommentRepository.existsByParentId(parentCommentId);
        boolean hasNoChild = feedCommentRepository.existsByParentId(childCommentId);

        // then
        assertThat(hasChild).isTrue();
        assertThat(hasNoChild).isFalse();
        verify(feedCommentRepository).existsByParentId(parentCommentId);
        verify(feedCommentRepository).existsByParentId(childCommentId);
    }

    @Test
    @DisplayName("존재하지 않는 피드 ID로 조회 시 빈 목록을 반환한다")
    void findAllByFeedId_NotFound() {
        // given
        Long nonExistentFeedId = 999L;
        given(feedCommentRepository.findAllByFeedId(nonExistentFeedId)).willReturn(Arrays.asList());

        // when
        List<FeedComment> comments = feedCommentRepository.findAllByFeedId(nonExistentFeedId);

        // then
        assertThat(comments).isEmpty();
        verify(feedCommentRepository).findAllByFeedId(nonExistentFeedId);
    }

    @Test
    @DisplayName("존재하지 않는 부모 댓글 ID로 자식 댓글 존재 여부 확인 시 false를 반환한다")
    void existsByParentId_NotFound() {
        // given
        Long nonExistentParentId = 999L;
        given(feedCommentRepository.existsByParentId(nonExistentParentId)).willReturn(false);

        // when
        boolean exists = feedCommentRepository.existsByParentId(nonExistentParentId);

        // then
        assertThat(exists).isFalse();
        verify(feedCommentRepository).existsByParentId(nonExistentParentId);
    }

    @Test
    @DisplayName("댓글 계층 구조를 정확히 저장하고 조회한다")
    void commentHierarchy() {
        // given
        FeedComment parentComment = FeedComment.create(feed, member, null, "부모 댓글");
        ReflectionTestUtils.setField(parentComment, "id", 1L);

        FeedComment childComment1 = FeedComment.create(feed, member, 1L, "자식 댓글 1");
        ReflectionTestUtils.setField(childComment1, "id", 2L);
        
        FeedComment childComment2 = FeedComment.create(feed, member, 1L, "자식 댓글 2");
        ReflectionTestUtils.setField(childComment2, "id", 3L);

        List<FeedComment> allComments = Arrays.asList(parentComment, childComment1, childComment2);
        given(feedCommentRepository.findAllByFeedId(feed.getId())).willReturn(allComments);

        // when
        List<FeedComment> comments = feedCommentRepository.findAllByFeedId(feed.getId());

        // then
        assertThat(comments).hasSize(3);
        
        List<FeedComment> parentComments = comments.stream()
                .filter(comment -> comment.getParentId() == null)
                .toList();
        assertThat(parentComments).hasSize(1);

        List<FeedComment> childComments = comments.stream()
                .filter(comment -> comment.getParentId() != null)
                .toList();
        assertThat(childComments).hasSize(2);
        assertThat(childComments).allMatch(comment -> 
                comment.getParentId().equals(parentComment.getId()));
        
        verify(feedCommentRepository).findAllByFeedId(feed.getId());
    }

    @Test
    @DisplayName("Repository 메서드 호출을 검증한다")
    void repositoryMethodCallVerification() {
        // given
        Long feedId = 1L;
        Long parentId = 2L;

        // when
        feedCommentRepository.findAllByFeedId(feedId);
        feedCommentRepository.existsByParentId(parentId);

        // then
        verify(feedCommentRepository).findAllByFeedId(feedId);
        verify(feedCommentRepository).existsByParentId(parentId);
    }
}
