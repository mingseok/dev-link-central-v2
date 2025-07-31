package dev.devlink.common.scheduler;

import dev.devlink.article.service.ArticleRankingService;
import dev.devlink.article.service.ArticleViewService;
import dev.devlink.common.redis.RedisConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleScheduler {

    private final ArticleViewService articleViewService;
    private final ArticleRankingService articleRankingService;

    @Scheduled(fixedRate = RedisConstants.SYNC_INTERVAL_MILLIS)
    public void syncViewCounts() {
        articleViewService.bulkUpdateViewCounts();
    }

    @Scheduled(fixedRate = RedisConstants.RANKING_REFRESH_INTERVAL)
    public void refreshArticleRankings() {
        articleRankingService.updateRankingScores();
    }
}
