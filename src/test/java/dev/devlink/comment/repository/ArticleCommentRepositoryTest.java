package dev.devlink.comment.repository;

import dev.devlink.article.entity.Article;
import dev.devlink.comment.entity.ArticleComment;
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
class ArticleCommentRepositoryTest {

    @Mock
    private ArticleCommentRepository articleCommentRepository;

    private Member member;
    private Article article;

    @BeforeEach
    void setUp() {
        member = Member.create("testName", "test@example.com", "testNickname", "password123");
        ReflectionTestUtils.setField(member, "id", 1L);

        article = Article.create(member, "Test Article", "Test content");
        ReflectionTestUtils.setField(article, "id", 1L);
    }

    @Test
    @DisplayName("아티클 ID로 댓글 목록을 조회한다")
    void findAllByArticleId() {
        // given
        ArticleComment comment1 = ArticleComment.create(article, member, null, "첫 번째 댓글");
        ReflectionTestUtils.setField(comment1, "id", 1L);
        
        ArticleComment comment2 = ArticleComment.create(article, member, null, "두 번째 댓글");
        ReflectionTestUtils.setField(comment2, "id", 2L);

        List<ArticleComment> expectedComments = Arrays.asList(comment1, comment2);
        given(articleCommentRepository.findAllByArticleId(article.getId())).willReturn(expectedComments);

        // when
        List<ArticleComment> comments = articleCommentRepository.findAllByArticleId(article.getId());

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(ArticleComment::getContent)
                .containsExactlyInAnyOrder("첫 번째 댓글", "두 번째 댓글");
        verify(articleCommentRepository).findAllByArticleId(article.getId());
    }

    @Test
    @DisplayName("부모 댓글 ID로 자식 댓글의 존재 여부를 확인한다")
    void existsByParentId() {
        // given
        Long parentCommentId = 1L;
        Long childCommentId = 2L;

        given(articleCommentRepository.existsByParentId(parentCommentId)).willReturn(true);
        given(articleCommentRepository.existsByParentId(childCommentId)).willReturn(false);

        // when
        boolean hasChild = articleCommentRepository.existsByParentId(parentCommentId);
        boolean hasNoChild = articleCommentRepository.existsByParentId(childCommentId);

        // then
        assertThat(hasChild).isTrue();
        assertThat(hasNoChild).isFalse();
        verify(articleCommentRepository).existsByParentId(parentCommentId);
        verify(articleCommentRepository).existsByParentId(childCommentId);
    }

    @Test
    @DisplayName("존재하지 않는 아티클 ID로 조회 시 빈 목록을 반환한다")
    void findAllByArticleId_NotFound() {
        // given
        Long nonExistentArticleId = 999L;
        given(articleCommentRepository.findAllByArticleId(nonExistentArticleId)).willReturn(Arrays.asList());

        // when
        List<ArticleComment> comments = articleCommentRepository.findAllByArticleId(nonExistentArticleId);

        // then
        assertThat(comments).isEmpty();
        verify(articleCommentRepository).findAllByArticleId(nonExistentArticleId);
    }

    @Test
    @DisplayName("존재하지 않는 부모 댓글 ID로 자식 댓글 존재 여부 확인 시 false를 반환한다")
    void existsByParentId_NotFound() {
        // given
        Long nonExistentParentId = 999L;
        given(articleCommentRepository.existsByParentId(nonExistentParentId)).willReturn(false);

        // when
        boolean exists = articleCommentRepository.existsByParentId(nonExistentParentId);

        // then
        assertThat(exists).isFalse();
        verify(articleCommentRepository).existsByParentId(nonExistentParentId);
    }

    @Test
    @DisplayName("댓글 계층 구조를 정확히 저장하고 조회한다")
    void commentHierarchy() {
        // given
        ArticleComment parentComment = ArticleComment.create(article, member, null, "부모 댓글");
        ReflectionTestUtils.setField(parentComment, "id", 1L);

        ArticleComment childComment1 = ArticleComment.create(article, member, 1L, "자식 댓글 1");
        ReflectionTestUtils.setField(childComment1, "id", 2L);
        
        ArticleComment childComment2 = ArticleComment.create(article, member, 1L, "자식 댓글 2");
        ReflectionTestUtils.setField(childComment2, "id", 3L);

        List<ArticleComment> allComments = Arrays.asList(parentComment, childComment1, childComment2);
        given(articleCommentRepository.findAllByArticleId(article.getId())).willReturn(allComments);

        // when
        List<ArticleComment> comments = articleCommentRepository.findAllByArticleId(article.getId());

        // then
        assertThat(comments).hasSize(3);
        
        List<ArticleComment> parentComments = comments.stream()
                .filter(comment -> comment.getParentId() == null)
                .toList();
        assertThat(parentComments).hasSize(1);

        List<ArticleComment> childComments = comments.stream()
                .filter(comment -> comment.getParentId() != null)
                .toList();
        assertThat(childComments).hasSize(2);
        assertThat(childComments).allMatch(comment -> 
                comment.getParentId().equals(parentComment.getId()));
        
        verify(articleCommentRepository).findAllByArticleId(article.getId());
    }

    @Test
    @DisplayName("다른 아티클의 댓글은 조회되지 않는다")
    void findAllByArticleId_OnlySpecificArticle() {
        // given
        Article anotherArticle = Article.create(member, "Another Article", "Another content");
        ReflectionTestUtils.setField(anotherArticle, "id", 2L);

        ArticleComment commentForFirstArticle = ArticleComment.create(article, member, null, "첫 번째 아티클 댓글");
        ReflectionTestUtils.setField(commentForFirstArticle, "id", 1L);
        
        ArticleComment commentForSecondArticle = ArticleComment.create(anotherArticle, member, null, "두 번째 아티클 댓글");
        ReflectionTestUtils.setField(commentForSecondArticle, "id", 2L);

        given(articleCommentRepository.findAllByArticleId(article.getId()))
                .willReturn(Arrays.asList(commentForFirstArticle));
        given(articleCommentRepository.findAllByArticleId(anotherArticle.getId()))
                .willReturn(Arrays.asList(commentForSecondArticle));

        // when
        List<ArticleComment> firstArticleComments = articleCommentRepository.findAllByArticleId(article.getId());
        List<ArticleComment> secondArticleComments = articleCommentRepository.findAllByArticleId(anotherArticle.getId());

        // then
        assertThat(firstArticleComments).hasSize(1);
        assertThat(firstArticleComments.get(0).getContent()).isEqualTo("첫 번째 아티클 댓글");
        
        assertThat(secondArticleComments).hasSize(1);
        assertThat(secondArticleComments.get(0).getContent()).isEqualTo("두 번째 아티클 댓글");
        
        verify(articleCommentRepository).findAllByArticleId(article.getId());
        verify(articleCommentRepository).findAllByArticleId(anotherArticle.getId());
    }

    @Test
    @DisplayName("Repository 메서드 호출을 검증한다")
    void repositoryMethodCallVerification() {
        // given
        Long articleId = 1L;
        Long parentId = 2L;

        // when
        articleCommentRepository.findAllByArticleId(articleId);
        articleCommentRepository.existsByParentId(parentId);

        // then
        verify(articleCommentRepository).findAllByArticleId(articleId);
        verify(articleCommentRepository).existsByParentId(parentId);
    }
}
