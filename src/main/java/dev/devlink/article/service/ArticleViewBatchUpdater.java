package dev.devlink.article.service;

import dev.devlink.article.service.dto.ViewCountUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleViewBatchUpdater {

    private static final int BATCH_SIZE = 500;
    private static final String PARAM_VIEW_COUNT = "viewCount";
    private static final String PARAM_ARTICLE_ID = "articleId";

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void batchUpdate(List<ViewCountUpdateDto> updateList) {
        if (updateList.isEmpty()) return;

        String sql = """
            UPDATE article 
            SET view_count = view_count + :viewCount 
            WHERE id = :articleId
            """;

        List<MapSqlParameterSource> parameterSources = new ArrayList<>();
        for (ViewCountUpdateDto dto : updateList) {
            MapSqlParameterSource source = new MapSqlParameterSource()
                    .addValue(PARAM_VIEW_COUNT, dto.getViewCount())
                    .addValue(PARAM_ARTICLE_ID, dto.getArticleId());
            parameterSources.add(source);
        }
        
        for (int startIndex = 0; startIndex < parameterSources.size(); startIndex += BATCH_SIZE) {
            int endIndex = Math.min(startIndex + BATCH_SIZE, parameterSources.size());
            List<MapSqlParameterSource> batch = parameterSources.subList(startIndex, endIndex);
            namedJdbcTemplate.batchUpdate(sql, batch.toArray(new MapSqlParameterSource[0]));
        }
    }
}
