package dev.devlink.article.service;

import dev.devlink.article.entity.Article;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.article.service.dto.response.ArticleListResponse;
import dev.devlink.common.redis.RedisKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArticleRankingServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ListOperations<String, String> listOperations;

    @InjectMocks
    private ArticleRankingService articleRankingService;

    @Test
    @DisplayName("상위 랭킹 게시글을 조회할 수 있다")
    void getTopRankedArticles_Success() {
        // given
        String key = RedisKey.getTopArticleListKey();
        List<String> articleIds = List.of("1", "2", "3");
        List<Long> longIds = List.of(1L, 2L, 3L);
        List<Article> articles = createMockArticles();

        given(redisTemplate.opsForList()).willReturn(listOperations);
        given(listOperations.range(key, 0, 4)).willReturn(articleIds);
        given(articleRepository.findAllById(longIds)).willReturn(articles);

        // when
        List<ArticleListResponse> result = articleRankingService.getTopRankedArticles();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getTitle()).isEqualTo("첫번째 게시글");
        assertThat(result.get(1).getTitle()).isEqualTo("두번째 게시글");
        assertThat(result.get(2).getTitle()).isEqualTo("세번째 게시글");
    }

    @Test
    @DisplayName("Redis에 데이터가 없으면 빈 목록을 반환한다")
    void getTopRankedArticles_EmptyRedis_ReturnsEmptyList() {
        // given
        String key = RedisKey.getTopArticleListKey();
        
        given(redisTemplate.opsForList()).willReturn(listOperations);
        given(listOperations.range(key, 0, 4)).willReturn(null);

        // when
        List<ArticleListResponse> result = articleRankingService.getTopRankedArticles();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Redis 상위 게시글 목록을 업데이트할 수 있다")
    void updateRedisTopArticles_Success() {
        // given
        String key = RedisKey.getTopArticleListKey();
        List<Article> articles = createMockArticles();
        
        given(redisTemplate.opsForList()).willReturn(listOperations);
        given(articleRepository.findTopByViews(any(PageRequest.class))).willReturn(articles);

        // when
        articleRankingService.updateRedisTopArticles();

        // then
        then(redisTemplate).should().delete(key);
        then(listOperations).should().rightPush(key, "1");
        then(listOperations).should().rightPush(key, "2");
        then(listOperations).should().rightPush(key, "3");
    }

    @Test
    @DisplayName("빈 게시글 목록으로도 Redis를 업데이트할 수 있다")
    void updateRedisTopArticles_EmptyList_Success() {
        // given
        String key = RedisKey.getTopArticleListKey();
        
        given(redisTemplate.opsForList()).willReturn(listOperations);
        given(articleRepository.findTopByViews(any(PageRequest.class))).willReturn(List.of());

        // when
        articleRankingService.updateRedisTopArticles();

        // then
        then(redisTemplate).should().delete(key);
    }

    private List<Article> createMockArticles() {
        Article article1 = mock(Article.class);
        Article article2 = mock(Article.class);
        Article article3 = mock(Article.class);

        LocalDateTime now = LocalDateTime.now();

        given(article1.getId()).willReturn(1L);
        given(article1.getTitle()).willReturn("첫번째 게시글");
        given(article1.getWriterNickname()).willReturn("작성자1");
        given(article1.getWriterId()).willReturn(1L);
        given(article1.getViewCount()).willReturn(100L);
        given(article1.getCreatedAt()).willReturn(now);
        given(article1.getIdAsString()).willReturn("1");

        given(article2.getId()).willReturn(2L);
        given(article2.getTitle()).willReturn("두번째 게시글");
        given(article2.getWriterNickname()).willReturn("작성자2");
        given(article2.getWriterId()).willReturn(2L);
        given(article2.getViewCount()).willReturn(80L);
        given(article2.getCreatedAt()).willReturn(now);
        given(article2.getIdAsString()).willReturn("2");

        given(article3.getId()).willReturn(3L);
        given(article3.getTitle()).willReturn("세번째 게시글");
        given(article3.getWriterNickname()).willReturn("작성자3");
        given(article3.getWriterId()).willReturn(3L);
        given(article3.getViewCount()).willReturn(60L);
        given(article3.getCreatedAt()).willReturn(now);
        given(article3.getIdAsString()).willReturn("3");

        return List.of(article1, article2, article3);
    }
}
