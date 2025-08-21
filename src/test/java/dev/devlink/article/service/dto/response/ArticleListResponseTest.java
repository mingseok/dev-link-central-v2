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
class ArticleListResponseTest {

    @Test
    @DisplayName("게시글로부터 응답 DTO를 생성할 수 있다")
    void from_WithArticle_Success() {
        // given
        Article article = mock(Article.class);
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 12, 0);

        given(article.getId()).willReturn(1L);
        given(article.getTitle()).willReturn("테스트 제목");
        given(article.getWriterNickname()).willReturn("김민석닉네임");
        given(article.getWriterId()).willReturn(1L);
        given(article.getViewCount()).willReturn(10L);
        given(article.getCreatedAt()).willReturn(createdAt);

        // when
        ArticleListResponse response = ArticleListResponse.from(article);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getWriter()).isEqualTo("김민석닉네임");
        assertThat(response.getWriterId()).isEqualTo(1L);
        assertThat(response.getViewCount()).isEqualTo(10L);
        assertThat(response.getFormattedCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("게시글과 총 조회수로부터 응답 DTO를 생성할 수 있다")
    void from_WithArticleAndTotalViewCount_Success() {
        // given
        Article article = mock(Article.class);
        Long totalViewCount = 100L;
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 12, 0);

        given(article.getId()).willReturn(1L);
        given(article.getTitle()).willReturn("테스트 제목");
        given(article.getWriterNickname()).willReturn("김민석닉네임");
        given(article.getWriterId()).willReturn(1L);
        given(article.getCreatedAt()).willReturn(createdAt);

        // when
        ArticleListResponse response = ArticleListResponse.from(article, totalViewCount);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getWriter()).isEqualTo("김민석닉네임");
        assertThat(response.getWriterId()).isEqualTo(1L);
        assertThat(response.getViewCount()).isEqualTo(totalViewCount);
        assertThat(response.getFormattedCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("생성자를 통해 응답 DTO를 생성할 수 있다")
    void constructor_Success() {
        // given
        Long id = 1L;
        String title = "제목";
        String writer = "작성자";
        Long writerId = 2L;
        String formattedCreatedAt = "2024-01-01 12:00:00";
        Long viewCount = 50L;

        // when
        ArticleListResponse response = new ArticleListResponse(
                id, title, writer, writerId, formattedCreatedAt, viewCount
        );

        // then
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getTitle()).isEqualTo(title);
        assertThat(response.getWriter()).isEqualTo(writer);
        assertThat(response.getWriterId()).isEqualTo(writerId);
        assertThat(response.getFormattedCreatedAt()).isEqualTo(formattedCreatedAt);
        assertThat(response.getViewCount()).isEqualTo(viewCount);
    }
}
