package dev.devlink.article.service;

import dev.devlink.common.redis.RedisKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArticleViewServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ArticleViewBatchUpdater batchUpdater;

    @Mock
    private SetOperations<String, String> setOperations;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private ArticleViewService articleViewService;

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForSet()).willReturn(setOperations);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    @DisplayName("새로운 사용자의 조회수를 증가시킬 수 있다")
    void increaseViewCount_NewUser_Success() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;
        String viewedKey = RedisKey.getViewedMembersKey(articleId);
        String viewCountKey = RedisKey.getArticleViewKey(articleId);
        String saveDbKey = RedisKey.articlesSaveDbKey();

        given(setOperations.add(viewedKey, String.valueOf(memberId))).willReturn(1L);

        // when
        articleViewService.increaseViewCount(articleId, memberId);

        // then
        then(redisTemplate).should().expire(eq(viewedKey), anyLong(), eq(TimeUnit.SECONDS));
        then(valueOperations).should().increment(viewCountKey);
        then(setOperations).should().add(saveDbKey, articleId.toString());
    }

    @Test
    @DisplayName("이미 조회한 사용자는 조회수가 증가하지 않는다")
    void increaseViewCount_ExistingUser_DoesNotIncrease() {
        // given
        Long articleId = 1L;
        Long memberId = 1L;
        String viewedKey = RedisKey.getViewedMembersKey(articleId);

        given(setOperations.add(viewedKey, String.valueOf(memberId))).willReturn(0L);

        // when
        articleViewService.increaseViewCount(articleId, memberId);

        // then
        then(valueOperations).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Redis와 DB 조회수를 합산하여 전체 조회수를 반환한다")
    void getTotalViewCount_WithRedisData_Success() {
        // given
        Long articleId = 1L;
        Long dbViewCount = 100L;
        String redisValue = "50";
        String viewCountKey = RedisKey.getArticleViewKey(articleId);

        given(valueOperations.get(viewCountKey)).willReturn(redisValue);

        // when
        Long totalViewCount = articleViewService.getTotalViewCount(articleId, dbViewCount);

        // then
        assertThat(totalViewCount).isEqualTo(150L);
    }

    @Test
    @DisplayName("Redis에 데이터가 없으면 DB 조회수만 반환한다")
    void getTotalViewCount_NoRedisData_ReturnsDbCount() {
        // given
        Long articleId = 1L;
        Long dbViewCount = 100L;
        String viewCountKey = RedisKey.getArticleViewKey(articleId);

        given(valueOperations.get(viewCountKey)).willReturn(null);

        // when
        Long totalViewCount = articleViewService.getTotalViewCount(articleId, dbViewCount);

        // then
        assertThat(totalViewCount).isEqualTo(100L);
    }

    @Test
    @DisplayName("조회수를 일괄 업데이트할 수 있다")
    void bulkUpdateViewCounts_Success() {
        // given
        String saveDbKey = RedisKey.articlesSaveDbKey();
        Set<String> articleIds = Set.of("1", "2", "3");
        
        given(setOperations.members(saveDbKey)).willReturn(articleIds);
        given(valueOperations.get("article:view:1")).willReturn("10");
        given(valueOperations.get("article:view:2")).willReturn("20");
        given(valueOperations.get("article:view:3")).willReturn("30");

        // when
        articleViewService.bulkUpdateViewCounts();

        // then
        then(batchUpdater).should().batchUpdate(anyList());
        then(redisTemplate).should().delete("article:view:1");
        then(redisTemplate).should().delete("article:view:2");
        then(redisTemplate).should().delete("article:view:3");
        then(setOperations).should().remove(saveDbKey, "1");
        then(setOperations).should().remove(saveDbKey, "2");
        then(setOperations).should().remove(saveDbKey, "3");
    }

    @Test
    @DisplayName("업데이트할 게시글이 없으면 아무 작업을 하지 않는다")
    void bulkUpdateViewCounts_NoArticles_DoesNothing() {
        // given
        String saveDbKey = RedisKey.articlesSaveDbKey();
        given(setOperations.members(saveDbKey)).willReturn(null);

        // when
        articleViewService.bulkUpdateViewCounts();

        // then
        then(batchUpdater).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("빈 Set이 반환되어도 아무 작업을 하지 않는다")
    void bulkUpdateViewCounts_EmptySet_DoesNothing() {
        // given
        String saveDbKey = RedisKey.articlesSaveDbKey();
        given(setOperations.members(saveDbKey)).willReturn(Set.of());

        // when
        articleViewService.bulkUpdateViewCounts();

        // then
        then(batchUpdater).shouldHaveNoInteractions();
    }
}
