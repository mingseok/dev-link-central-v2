package dev.devlink.article.service.dto.response;

import dev.devlink.article.entity.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArticleDetailResponseTest {

    @Test
    @DisplayName("게시글로부터 응답 DTO를 생성할 수 있다")
    void from_Success() {
        // given
        Article article = mock(Article.class);
        Long memberId = 1L;
        Long viewsCount = 100L;
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 1, 12, 0);

        given(article.getId()).willReturn(1L);
        given(article.getTitle()).willReturn("테스트 제목");
        given(article.getContent()).willReturn("테스트 내용");
        given(article.getWriterNickname()).willReturn("김민석닉네임");
        given(article.getWriterId()).willReturn(1L);
        given(article.getUpdatedAt()).willReturn(updatedAt);
        given(article.getCreatedAt()).willReturn(updatedAt);
        given(article.isAuthor(memberId)).willReturn(true);

        // when
        ArticleDetailResponse response = ArticleDetailResponse.from(article, memberId, viewsCount);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getContent()).isEqualTo("테스트 내용");
        assertThat(response.getWriter()).isEqualTo("김민석닉네임");
        assertThat(response.getWriterId()).isEqualTo(1L);
        assertThat(response.getViewsCount()).isEqualTo(viewsCount);
        assertThat(response.getModifiedAt()).isEqualTo(updatedAt);
        assertThat(response.getFormattedCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("작성자일 때 isAuthor가 true로 설정된다")
    void from_Author_IsAuthorTrue() {
        // given
        Article article = mock(Article.class);
        Long authorId = 1L;
        Long viewsCount = 50L;
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0);

        given(article.getId()).willReturn(1L);
        given(article.getTitle()).willReturn("테스트 제목");
        given(article.getContent()).willReturn("테스트 내용");
        given(article.getWriterNickname()).willReturn("김민석닉네임");
        given(article.getWriterId()).willReturn(authorId);
        given(article.getUpdatedAt()).willReturn(dateTime);
        given(article.getCreatedAt()).willReturn(dateTime);
        given(article.isAuthor(authorId)).willReturn(true);

        // when
        ArticleDetailResponse response = ArticleDetailResponse.from(article, authorId, viewsCount);

        // then
        assertThat(response.isAuthor()).isTrue();
    }

    @Test
    @DisplayName("작성자가 아닐 때 isAuthor가 false로 설정된다")
    void from_NotAuthor_IsAuthorFalse() {
        // given
        Article article = mock(Article.class);
        Long authorId = 1L;
        Long nonAuthorId = 999L;
        Long viewsCount = 50L;
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0);

        given(article.getId()).willReturn(1L);
        given(article.getTitle()).willReturn("테스트 제목");
        given(article.getContent()).willReturn("테스트 내용");
        given(article.getWriterNickname()).willReturn("김민석닉네임");
        given(article.getWriterId()).willReturn(authorId);
        given(article.getUpdatedAt()).willReturn(dateTime);
        given(article.getCreatedAt()).willReturn(dateTime);
        given(article.isAuthor(nonAuthorId)).willReturn(false);

        // when
        ArticleDetailResponse response = ArticleDetailResponse.from(article, nonAuthorId, viewsCount);

        // then
        assertThat(response.isAuthor()).isFalse();
    }

    @Test
    @DisplayName("memberId가 null일 때 isAuthor가 false로 설정된다")
    void from_NullMemberId_IsAuthorFalse() {
        // given
        Article article = mock(Article.class);
        Long viewsCount = 50L;
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0);

        given(article.getId()).willReturn(1L);
        given(article.getTitle()).willReturn("테스트 제목");
        given(article.getContent()).willReturn("테스트 내용");
        given(article.getWriterNickname()).willReturn("김민석닉네임");
        given(article.getWriterId()).willReturn(1L);
        given(article.getUpdatedAt()).willReturn(dateTime);
        given(article.getCreatedAt()).willReturn(dateTime);
        given(article.isAuthor(null)).willReturn(false);

        // when
        ArticleDetailResponse response = ArticleDetailResponse.from(article, null, viewsCount);

        // then
        assertThat(response.isAuthor()).isFalse();
    }
}
