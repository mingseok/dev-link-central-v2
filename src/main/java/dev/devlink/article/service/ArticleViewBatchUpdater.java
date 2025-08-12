package dev.devlink.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ArticleViewBatchUpdater {

    private static final int BATCH_SIZE = 500;

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void batchUpdate(Map<Long, Long> viewCounts) {
        if (viewCounts.isEmpty()) return;

        String sql = """
            UPDATE article 
            SET view_count = view_count + :viewCount 
            WHERE id = :articleId
            """;

        List<MapSqlParameterSource> parameterSources = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : viewCounts.entrySet()) {
            MapSqlParameterSource source = new MapSqlParameterSource()
                    .addValue("viewCount", entry.getValue())
                    .addValue("articleId", entry.getKey());
            parameterSources.add(source);
        }

        for (int i = 0; i < parameterSources.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, parameterSources.size());
            List<MapSqlParameterSource> batch = parameterSources.subList(i, endIndex);
            namedJdbcTemplate.batchUpdate(sql, batch.toArray(new MapSqlParameterSource[0]));
        }
    }
}
