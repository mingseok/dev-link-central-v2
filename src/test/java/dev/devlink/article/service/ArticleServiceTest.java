package dev.devlink.article.service;

import dev.devlink.article.entity.Article;
import dev.devlink.article.exception.ArticleError;
import dev.devlink.article.exception.ArticleException;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.article.service.dto.request.ArticleCreateRequest;
import dev.devlink.article.service.dto.request.ArticleUpdateRequest;
import dev.devlink.article.service.dto.response.ArticleDetailResponse;
import dev.devlink.article.service.dto.response.ArticleListResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArticleServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleViewService articleViewService;

    @InjectMocks
    private ArticleService articleService;

    private Member member;
    private Article article;
    private ArticleCreateRequest createRequest;
    private ArticleUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        member = mock(Member.class);
        article = mock(Article.class);

        given(member.getId()).willReturn(1L);
        given(member.getNickname()).willReturn("김민석닉네임");

        LocalDateTime now = LocalDateTime.now();
        given(article.getId()).willReturn(1L);
        given(article.getTitle()).willReturn("테스트 제목");
        given(article.getContent()).willReturn("테스트 내용");
        given(article.getWriterId()).willReturn(1L);
        given(article.getWriterNickname()).willReturn("김민석닉네임");
        given(article.getViewCount()).willReturn(0L);
        given(article.getCreatedAt()).willReturn(now);
        given(article.getUpdatedAt()).willReturn(now);

        createRequest = new ArticleCreateRequest("새 게시글 제목", "새 게시글 내용");
        updateRequest = new ArticleUpdateRequest("수정된 제목", "수정된 내용");
    }

    @Test
    @DisplayName("게시글을 저장할 수 있다")
    void save_Success() {
        // given
        Long memberId = 1L;
        given(memberService.findMemberById(memberId)).willReturn(member);

        // when
        articleService.save(createRequest, memberId);

        // then
        then(articleRepository).should().save(any(Article.class));
    }

    @Test
    @DisplayName("게시글 상세를 조회할 수 있다")
    void findDetail_Success() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;
        Long totalViewCount = 10L;

        given(articleRepository.findDetailById(articleId)).willReturn(Optional.of(article));
        given(articleViewService.getTotalViewCount(articleId, article.getViewCount())).willReturn(totalViewCount);

        // when
        ArticleDetailResponse response = articleService.findDetail(articleId, memberId);

        // then
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getContent()).isEqualTo("테스트 내용");
        then(articleViewService).should().increaseViewCount(articleId, memberId);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회시 예외가 발생한다")
    void findDetail_ArticleNotFound_ThrowsException() {
        // given
        Long articleId = 999L;
        Long memberId = 1L;
        given(articleRepository.findDetailById(articleId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> articleService.findDetail(articleId, memberId))
                .isInstanceOf(ArticleException.class);
    }

    @Test
    @DisplayName("페이지별 게시글 목록을 조회할 수 있다")
    void findArticlesByPage_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Article> articles = List.of(article);
        Page<Article> articlePage = new PageImpl<>(articles, pageable, 1);

        given(articleRepository.findAllWithMember(any(Pageable.class))).willReturn(articlePage);

        // when
        Page<ArticleListResponse> result = articleService.findArticlesByPage(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("게시글을 수정할 수 있다")
    void update_Success() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when
        articleService.update(updateRequest, articleId, memberId);

        // then
        then(article).should().checkAuthor(memberId);
        then(article).should().update(updateRequest.getTitle(), updateRequest.getContent());
    }

    @Test
    @DisplayName("작성자가 아니면 게시글 수정시 예외가 발생한다")
    void update_NotAuthor_ThrowsException() {
        // given
        Long articleId = 1L;
        Long otherMemberId = 999L;
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        doThrow(new ArticleException(ArticleError.NO_PERMISSION)).when(article).checkAuthor(otherMemberId);

        // when & then
        assertThatThrownBy(() -> articleService.update(updateRequest, articleId, otherMemberId))
                .isInstanceOf(ArticleException.class);
    }

    @Test
    @DisplayName("게시글을 삭제할 수 있다")
    void delete_Success() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when
        articleService.delete(articleId, memberId);

        // then
        then(article).should().checkAuthor(memberId);
        then(articleRepository).should().delete(article);
    }

    @Test
    @DisplayName("작성자가 아니면 게시글 삭제시 예외가 발생한다")
    void delete_NotAuthor_ThrowsException() {
        // given
        Long articleId = 1L;
        Long otherMemberId = 999L;
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        doThrow(new ArticleException(ArticleError.NO_PERMISSION)).when(article).checkAuthor(otherMemberId);

        // when & then
        assertThatThrownBy(() -> articleService.delete(articleId, otherMemberId))
                .isInstanceOf(ArticleException.class);
    }

    @Test
    @DisplayName("게시글 ID로 게시글을 조회할 수 있다")
    void findArticleById_Success() {
        // given
        Long articleId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when
        Article foundArticle = articleService.findArticleById(articleId);

        // then
        assertThat(foundArticle).isEqualTo(article);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 조회시 예외가 발생한다")
    void findArticleById_NotFound_ThrowsException() {
        // given
        Long articleId = 999L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> articleService.findArticleById(articleId))
                .isInstanceOf(ArticleException.class);
    }
}
