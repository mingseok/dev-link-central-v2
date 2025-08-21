package dev.devlink.article.service;

import dev.devlink.article.constant.LikeStatus;
import dev.devlink.article.entity.Article;
import dev.devlink.article.entity.ArticleLike;
import dev.devlink.article.exception.ArticleException;
import dev.devlink.article.repository.ArticleLikeRepository;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ArticleLikeServiceTest {

    @Mock
    private ArticleLikeRepository articleLikeRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private ArticleLikeService articleLikeService;

    private Member member;
    private Article article;

    @BeforeEach
    void setUp() {
        member = Member.create(
                "김민석",
                "test@example.com",
                "김민석닉네임",
                "encodedPassword"
        );

        article = Article.create(member, "테스트 제목", "테스트 내용");
    }

    @Test
    @DisplayName("좋아요를 추가할 수 있다")
    void likeOrCancel_AddLike_Success() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        given(articleLikeRepository.findByArticleAndMember(article, member)).willReturn(Optional.empty());

        // when
        LikeStatus result = articleLikeService.likeOrCancel(articleId, memberId);

        // then
        assertThat(result).isEqualTo(LikeStatus.LIKE_ADDED);
        then(articleLikeRepository).should().save(any(ArticleLike.class));
    }

    @Test
    @DisplayName("좋아요를 취소할 수 있다")
    void likeOrCancel_RemoveLike_Success() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;
        ArticleLike existingLike = ArticleLike.create(article, member);

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        given(articleLikeRepository.findByArticleAndMember(article, member)).willReturn(Optional.of(existingLike));

        // when
        LikeStatus result = articleLikeService.likeOrCancel(articleId, memberId);

        // then
        assertThat(result).isEqualTo(LikeStatus.LIKE_REMOVED);
        then(articleLikeRepository).should().delete(existingLike);
    }

    @Test
    @DisplayName("동시성 문제로 좋아요 추가 실패시 좋아요를 삭제한다")
    void likeOrCancel_ConcurrencyIssue_RemoveLike() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        given(articleLikeRepository.findByArticleAndMember(article, member)).willReturn(Optional.empty());
        given(articleLikeRepository.save(any(ArticleLike.class))).willThrow(new DataIntegrityViolationException("Duplicate key"));

        // when
        LikeStatus result = articleLikeService.likeOrCancel(articleId, memberId);

        // then
        assertThat(result).isEqualTo(LikeStatus.LIKE_REMOVED);
        then(articleLikeRepository).should().deleteByArticleAndMember(article, member);
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 좋아요 요청시 예외가 발생한다")
    void likeOrCancel_ArticleNotFound_ThrowsException() {
        // given
        Long articleId = 999L;
        Long memberId = 1L;

        given(memberService.findMemberById(memberId)).willReturn(member);
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> articleLikeService.likeOrCancel(articleId, memberId))
                .isInstanceOf(ArticleException.class);
    }

    @Test
    @DisplayName("게시글의 좋아요 수를 조회할 수 있다")
    void countLikes_Success() {
        // given
        Long articleId = 1L;
        long expectedCount = 10L;

        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        given(articleLikeRepository.countByArticle(article)).willReturn(expectedCount);

        // when
        long result = articleLikeService.countLikes(articleId);

        // then
        assertThat(result).isEqualTo(expectedCount);
    }

    @Test
    @DisplayName("존재하지 않는 게시글의 좋아요 수 조회시 예외가 발생한다")
    void countLikes_ArticleNotFound_ThrowsException() {
        // given
        Long articleId = 999L;

        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> articleLikeService.countLikes(articleId))
                .isInstanceOf(ArticleException.class);
    }
}
