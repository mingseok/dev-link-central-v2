package dev.devlink.article.service;

import dev.devlink.article.service.dto.ViewCountUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ArticleViewBatchUpdaterTest {

    @Mock
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @InjectMocks
    private ArticleViewBatchUpdater batchUpdater;

    @Test
    @DisplayName("조회수 업데이트 목록을 배치로 처리할 수 있다")
    void batchUpdate_Success() {
        // given
        List<ViewCountUpdateDto> updateList = List.of(
                new ViewCountUpdateDto(1L, 10L),
                new ViewCountUpdateDto(2L, 20L),
                new ViewCountUpdateDto(3L, 30L)
        );

        // when
        batchUpdater.batchUpdate(updateList);

        // then
        then(namedJdbcTemplate).should().batchUpdate(anyString(), any(MapSqlParameterSource[].class));
    }

    @Test
    @DisplayName("빈 목록으로 배치 업데이트시 아무 작업을 하지 않는다")
    void batchUpdate_EmptyList_DoesNothing() {
        // given
        List<ViewCountUpdateDto> updateList = List.of();

        // when
        batchUpdater.batchUpdate(updateList);

        // then
        then(namedJdbcTemplate).should(never()).batchUpdate(anyString(), any(MapSqlParameterSource[].class));
    }

    @Test
    @DisplayName("대용량 데이터를 여러 배치로 나누어 처리할 수 있다")
    void batchUpdate_LargeData_SplitIntoBatches() {
        // given - 1000개의 데이터 생성 (배치 크기 500보다 큰 데이터)
        List<ViewCountUpdateDto> updateList = new ArrayList<>();
        for (long i = 1; i <= 1000; i++) {
            updateList.add(new ViewCountUpdateDto(i, i * 10));
        }

        // when
        batchUpdater.batchUpdate(updateList);

        // then - 2번의 배치 업데이트가 실행되어야 함
        then(namedJdbcTemplate).should(times(2)).batchUpdate(anyString(), any(MapSqlParameterSource[].class));
    }

    @Test
    @DisplayName("배치 크기보다 작은 데이터는 한 번에 처리된다")
    void batchUpdate_SmallData_SingleBatch() {
        // given - 100개의 데이터 생성 (배치 크기 500보다 작은 데이터)
        List<ViewCountUpdateDto> updateList = new ArrayList<>();
        for (long i = 1; i <= 100; i++) {
            updateList.add(new ViewCountUpdateDto(i, i * 5));
        }

        // when
        batchUpdater.batchUpdate(updateList);

        // then - 1번의 배치 업데이트만 실행되어야 함
        then(namedJdbcTemplate).should(times(1)).batchUpdate(anyString(), any(MapSqlParameterSource[].class));
    }

    @Test
    @DisplayName("정확히 배치 크기와 같은 데이터를 처리할 수 있다")
    void batchUpdate_ExactBatchSize_SingleBatch() {
        // given - 정확히 500개의 데이터 생성
        List<ViewCountUpdateDto> updateList = new ArrayList<>();
        for (long i = 1; i <= 500; i++) {
            updateList.add(new ViewCountUpdateDto(i, i * 2));
        }

        // when
        batchUpdater.batchUpdate(updateList);

        // then - 1번의 배치 업데이트만 실행되어야 함
        then(namedJdbcTemplate).should(times(1)).batchUpdate(anyString(), any(MapSqlParameterSource[].class));
    }
}
