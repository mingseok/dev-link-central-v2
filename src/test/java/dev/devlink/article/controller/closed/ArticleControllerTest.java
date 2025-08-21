package dev.devlink.article.controller.closed;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.devlink.article.constant.LikeStatus;
import dev.devlink.article.service.ArticleLikeService;
import dev.devlink.article.service.ArticleService;
import dev.devlink.article.service.dto.request.ArticleCreateRequest;
import dev.devlink.article.service.dto.request.ArticleUpdateRequest;
import dev.devlink.article.service.dto.response.ArticleDetailResponse;
import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @Mock
    private ArticleLikeService articleLikeService;

    @InjectMocks
    private ArticleController articleController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(articleController)
                .setCustomArgumentResolvers(new AuthMemberIdArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("게시글을 생성할 수 있다")
    void create_Success() throws Exception {
        // given
        Long memberId = 1L;
        ArticleCreateRequest request = new ArticleCreateRequest("테스트 제목", "테스트 내용");

        // when & then
        mockMvc.perform(post("/api/v1/articles")
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        then(articleService).should().save(any(ArticleCreateRequest.class), anyLong());
    }

    @Test
    @DisplayName("게시글을 수정할 수 있다")
    void update_Success() throws Exception {
        // given
        Long memberId = 1L;
        Long articleId = 1L;
        
        String requestJson = """
                {
                    "title": "수정된 제목",
                    "content": "수정된 내용"
                }
                """;

        // when & then
        mockMvc.perform(put("/api/v1/articles/{id}", articleId)
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        then(articleService).should().update(any(ArticleUpdateRequest.class), anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글을 삭제할 수 있다")
    void delete_Success() throws Exception {
        // given
        Long memberId = 1L;
        Long articleId = 1L;

        // when & then
        mockMvc.perform(delete("/api/v1/articles/{id}", articleId)
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        then(articleService).should().delete(articleId, memberId);
    }

    @Test
    @DisplayName("게시글 상세를 조회할 수 있다")
    void findDetail_Success() throws Exception {
        // given
        Long memberId = 1L;
        Long articleId = 1L;
        
        ArticleDetailResponse response = new ArticleDetailResponse(
                articleId,
                "테스트 제목",
                "테스트 내용",
                "김민석닉네임",
                2L, // writerId
                "2024-01-01 12:00:00",
                LocalDateTime.now(),
                10L,
                true
        );

        given(articleService.findDetail(articleId, memberId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/articles/{id}", articleId)
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.title").value("테스트 제목"))
                .andExpect(jsonPath("$.data.content").value("테스트 내용"))
                .andExpect(jsonPath("$.data.writer").value("김민석닉네임"))
                .andExpect(jsonPath("$.data.isAuthor").value(true));
    }

    @Test
    @DisplayName("게시글 좋아요를 처리할 수 있다")
    void likeOrCancel_Success() throws Exception {
        // given
        Long memberId = 1L;
        Long articleId = 1L;
        LikeStatus likeStatus = LikeStatus.LIKE_ADDED;

        given(articleLikeService.likeOrCancel(articleId, memberId)).willReturn(likeStatus);

        // when & then
        mockMvc.perform(post("/api/v1/articles/{id}/likes", articleId)
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value("LIKE_ADDED"));
    }

    @Test
    @DisplayName("유효하지 않은 제목으로 게시글 생성시 에러가 발생한다")
    void create_InvalidTitle_BadRequest() throws Exception {
        // given
        Long memberId = 1L;
        ArticleCreateRequest request = new ArticleCreateRequest("", "테스트 내용");

        // when & then
        mockMvc.perform(post("/api/v1/articles")
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유효하지 않은 내용으로 게시글 생성시 에러가 발생한다")
    void create_InvalidContent_BadRequest() throws Exception {
        // given
        Long memberId = 1L;
        ArticleCreateRequest request = new ArticleCreateRequest("테스트 제목", "");

        // when & then
        mockMvc.perform(post("/api/v1/articles")
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
