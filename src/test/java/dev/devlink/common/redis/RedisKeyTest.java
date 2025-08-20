package dev.devlink.common.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RedisKeyTest {

    @Test
    @DisplayName("게시글 조회수 키를 올바르게 생성한다")
    void getArticleViewKey_ReturnsCorrectKey() {
        // given
        Long articleId = 123L;

        // when
        String key = RedisKey.getArticleViewKey(articleId);

        // then
        assertThat(key).isEqualTo("article:view:123");
    }

    @Test
    @DisplayName("인기글 목록 키를 올바르게 생성한다")
    void getTopArticleListKey_ReturnsCorrectKey() {
        // when
        String key = RedisKey.getTopArticleListKey();

        // then
        assertThat(key).isEqualTo("article:top:list");
    }

    @Test
    @DisplayName("조회한 사용자 목록 키를 올바르게 생성한다")
    void getViewedMembersKey_ReturnsCorrectKey() {
        // given
        Long articleId = 456L;

        // when
        String key = RedisKey.getViewedMembersKey(articleId);

        // then
        assertThat(key).isEqualTo("article:viewed:members:456");
    }

    @Test
    @DisplayName("DB 저장 대상 키를 올바르게 생성한다")
    void articlesSaveDbKey_ReturnsCorrectKey() {
        // when
        String key = RedisKey.articlesSaveDbKey();

        // then
        assertThat(key).isEqualTo("articles:save:db");
    }

    @Test
    @DisplayName("서로 다른 게시글 ID로 다른 키를 생성한다")
    void differentArticleIds_GenerateDifferentKeys() {
        // given
        Long articleId1 = 1L;
        Long articleId2 = 2L;

        // when
        String key1 = RedisKey.getArticleViewKey(articleId1);
        String key2 = RedisKey.getArticleViewKey(articleId2);

        // then
        assertThat(key1).isNotEqualTo(key2);
        assertThat(key1).isEqualTo("article:view:1");
        assertThat(key2).isEqualTo("article:view:2");
    }

    @Test
    @DisplayName("큰 숫자 ID도 올바르게 처리한다")
    void largeArticleId_HandledCorrectly() {
        // given
        Long largeArticleId = 999999999L;

        // when
        String viewKey = RedisKey.getArticleViewKey(largeArticleId);
        String membersKey = RedisKey.getViewedMembersKey(largeArticleId);

        // then
        assertThat(viewKey).isEqualTo("article:view:999999999");
        assertThat(membersKey).isEqualTo("article:viewed:members:999999999");
    }
}
