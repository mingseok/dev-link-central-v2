package dev.devlink.article.controller.open;

import dev.devlink.article.service.ArticleLikeService;
import dev.devlink.article.service.ArticleRankingService;
import dev.devlink.article.service.ArticleService;
import dev.devlink.article.service.dto.response.ArticleListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ArticlePublicControllerTest {

    @Mock
    private ArticleService articleService;

    @Mock
    private ArticleLikeService articleLikeService;

    @Mock
    private ArticleRankingService articleRankingService;

    @InjectMocks
    private ArticlePublicController articlePublicController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(articlePublicController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("페이지별 게시글 목록을 조회할 수 있다")
    void getPagedArticles_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 8);
        
        ArticleListResponse response = new ArticleListResponse(
                1L,
                "테스트 제목",
                "김민석닉네임",
                1L,
                "2024-01-01 12:00:00",
                10L
        );

        Page<ArticleListResponse> articlePage = new PageImpl<>(List.of(response), pageable, 1);
        given(articleService.findArticlesByPage(any(Pageable.class))).willReturn(articlePage);

        // when & then
        mockMvc.perform(get("/api/public/articles")
                        .param("page", "0")
                        .param("size", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].title").value("테스트 제목"))
                .andExpect(jsonPath("$.data.content[0].writer").value("김민석닉네임"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("게시글의 좋아요 수를 조회할 수 있다")
    void countLikes_Success() throws Exception {
        // given
        Long articleId = 1L;
        long likeCount = 10L;
        given(articleLikeService.countLikes(articleId)).willReturn(likeCount);

        // when & then
        mockMvc.perform(get("/api/public/articles/{id}/likes", articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(10));
    }

    @Test
    @DisplayName("상위 랭킹 게시글을 조회할 수 있다")
    void findTopRankedArticles_Success() throws Exception {
        // given
        List<ArticleListResponse> topArticles = List.of(
                new ArticleListResponse(
                        1L,
                        "1위 게시글",
                        "김민석닉네임",
                        1L,
                        "2024-01-01 12:00:00",
                        1000L
                ),
                new ArticleListResponse(
                        2L,
                        "2위 게시글",
                        "김선우닉네임",
                        2L,
                        "2024-01-01 11:00:00",
                        500L
                )
        );

        given(articleRankingService.getTopRankedArticles()).willReturn(topArticles);

        // when & then
        mockMvc.perform(get("/api/public/articles/best"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("1위 게시글"))
                .andExpect(jsonPath("$.data[0].viewCount").value(1000))
                .andExpect(jsonPath("$.data[1].title").value("2위 게시글"))
                .andExpect(jsonPath("$.data[1].viewCount").value(500));
    }

    @Test
    @DisplayName("빈 랭킹 목록도 정상적으로 반환한다")
    void findTopRankedArticles_EmptyList_Success() throws Exception {
        // given
        given(articleRankingService.getTopRankedArticles()).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/public/articles/best"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
