package dev.devlink.comment.service;

import dev.devlink.article.entity.Article;
import dev.devlink.article.service.ArticleService;
import dev.devlink.comment.entity.ArticleComment;
import dev.devlink.comment.exception.CommentError;
import dev.devlink.comment.exception.CommentException;
import dev.devlink.comment.repository.ArticleCommentRepository;
import dev.devlink.comment.service.dto.request.CommentCreateRequest;
import dev.devlink.comment.service.dto.response.CommentResponse;
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
class ArticleCommentServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private ArticleService articleService;

    @Mock
    private ArticleCommentRepository commentRepository;

    @InjectMocks
    private ArticleCommentService articleCommentService;

    private Member member;
    private Article article;
    private CommentCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        member = Member.create("testName", "test@example.com", "testNickname", "password123");
        ReflectionTestUtils.setField(member, "id", 1L);
        
        article = Article.create(member, "Test Article", "Test content");
        ReflectionTestUtils.setField(article, "id", 1L);

        createRequest = new CommentCreateRequest();
        ReflectionTestUtils.setField(createRequest, "content", "테스트 댓글입니다.");
        ReflectionTestUtils.setField(createRequest, "parentId", null);
    }

    @Test
    @DisplayName("댓글을 정상적으로 저장한다")
    void save_Success() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(articleService.findArticleById(articleId)).willReturn(article);

        // when
        articleCommentService.save(createRequest, articleId, memberId);

        // then
        verify(memberService).findMemberById(memberId);
        verify(articleService).findArticleById(articleId);
        verify(commentRepository).save(any(ArticleComment.class));
    }

    @Test
    @DisplayName("대댓글을 정상적으로 저장한다")
    void save_ReplyComment_Success() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;
        Long parentId = 2L;

        ReflectionTestUtils.setField(createRequest, "parentId", parentId);

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(articleService.findArticleById(articleId)).willReturn(article);
        given(commentRepository.existsById(parentId)).willReturn(true);

        // when
        articleCommentService.save(createRequest, articleId, memberId);

        // then
        verify(commentRepository).save(any(ArticleComment.class));
    }

    @Test
    @DisplayName("존재하지 않는 부모 댓글 ID로 대댓글 저장 시 예외가 발생한다")
    void save_ParentCommentNotFound() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;
        Long parentId = 999L;

        ReflectionTestUtils.setField(createRequest, "parentId", parentId);

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(articleService.findArticleById(articleId)).willReturn(article);
        given(commentRepository.existsById(parentId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> articleCommentService.save(createRequest, articleId, memberId))
                .isInstanceOf(CommentException.class)
                .hasMessage(CommentError.PARENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("아티클의 댓글 목록을 정상적으로 조회한다")
    void getComments_Success() {
        // given
        Long articleId = 1L;

        ArticleComment parentComment = ArticleComment.create(article, member, null, "부모 댓글");
        ReflectionTestUtils.setField(parentComment, "id", 1L);
        
        ArticleComment childComment = ArticleComment.create(article, member, 1L, "자식 댓글");
        ReflectionTestUtils.setField(childComment, "id", 2L);

        List<ArticleComment> comments = Arrays.asList(parentComment, childComment);

        given(articleService.findArticleById(articleId)).willReturn(article);
        given(commentRepository.findAllByArticleId(articleId)).willReturn(comments);

        // when
        List<CommentResponse> result = articleCommentService.getComments(articleId);

        // then
        assertThat(result).hasSize(1); // 부모 댓글만 루트로 반환
        assertThat(result.get(0).getChildren()).hasSize(1); // 자식 댓글 1개
    }

    @Test
    @DisplayName("댓글을 정상적으로 삭제한다")
    void delete_Success() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        ArticleComment comment = ArticleComment.create(article, member, null, "삭제할 댓글");
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentRepository.existsByParentId(commentId)).willReturn(false);

        // when
        articleCommentService.delete(commentId, memberId);

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
        assertThatThrownBy(() -> articleCommentService.delete(commentId, memberId))
                .isInstanceOf(CommentException.class)
                .hasMessage(CommentError.NOT_FOUND.getMessage());
    }
}
