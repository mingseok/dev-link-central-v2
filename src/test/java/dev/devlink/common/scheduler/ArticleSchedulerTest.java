package dev.devlink.common.scheduler;

import dev.devlink.article.service.ArticleRankingService;
import dev.devlink.article.service.ArticleViewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ArticleSchedulerTest {

    @Mock
    private ArticleViewService articleViewService;

    @Mock
    private ArticleRankingService articleRankingService;

    @InjectMocks
    private ArticleScheduler articleScheduler;

    @Test
    @DisplayName("조회수 동기화 스케줄러가 정상적으로 실행된다")
    void syncViewCounts_ExecutesSuccessfully() {
        // when
        articleScheduler.syncViewCounts();

        // then
        then(articleViewService).should().bulkUpdateViewCounts();
    }

    @Test
    @DisplayName("인기글 동기화 스케줄러가 정상적으로 실행된다")
    void syncTopArticlesToRedis_ExecutesSuccessfully() {
        // when
        articleScheduler.syncTopArticlesToRedis();

        // then
        then(articleRankingService).should().updateRedisTopArticles();
    }
}
