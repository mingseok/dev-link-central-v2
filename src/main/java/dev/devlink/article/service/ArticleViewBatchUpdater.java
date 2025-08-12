package dev.devlink.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleViewBatchUpdater {

    private static final int BATCH_SIZE = 500;

    private final JdbcTemplate jdbcTemplate;

    public void batchUpdate(List<Object[]> batchArgs) {
        if (batchArgs.isEmpty()) return;

        String sql = "UPDATE article SET view_count = view_count + ? WHERE id = ?";
        
        for (int startIndex = 0; startIndex < batchArgs.size(); startIndex += BATCH_SIZE) {
            int endIndex = Math.min(startIndex + BATCH_SIZE, batchArgs.size());
            List<Object[]> batch = batchArgs.subList(startIndex, endIndex);
            jdbcTemplate.batchUpdate(sql, batch);
        }
    }
}
