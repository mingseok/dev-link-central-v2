package dev.devlink.comment.controller.closed;

import dev.devlink.comment.service.FeedCommentService;
import dev.devlink.comment.service.dto.request.CommentCreateRequest;
import dev.devlink.comment.service.dto.response.CommentResponse;
import dev.devlink.common.exception.GlobalExceptionHandler;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FeedCommentControllerTest {

    @Mock
    private FeedCommentService feedCommentService;

    @InjectMocks
    private FeedCommentController feedCommentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(feedCommentController)
                .setCustomArgumentResolvers(new AuthMemberIdArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("피드 댓글을 생성할 수 있다")
    void createComment_Success() throws Exception {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        
        String requestJson = """
                {
                    "content": "테스트 댓글입니다.",
                    "parentId": null
                }
                """;

        doNothing().when(feedCommentService).save(any(CommentCreateRequest.class), eq(feedId), eq(memberId));

        // when & then
        mockMvc.perform(post("/api/v1/feeds/{feedId}/comments", feedId)
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        then(feedCommentService).should().save(any(CommentCreateRequest.class), eq(feedId), eq(memberId));
    }

    @Test
    @DisplayName("피드 댓글 목록을 조회할 수 있다")
    void getComments_Success() throws Exception {
        // given
        Long feedId = 1L;
        CommentResponse commentResponse = CommentResponse.builder()
                .id(1L)
                .content("테스트 댓글입니다.")
                .writer("testNickname")
                .writerId(1L)
                .parentId(null)
                .createdAt(LocalDateTime.now())
                .children(Collections.emptyList())
                .build();

        List<CommentResponse> comments = List.of(commentResponse);
        given(feedCommentService.getComments(feedId)).willReturn(comments);

        // when & then
        mockMvc.perform(get("/api/v1/feeds/{feedId}/comments", feedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].content").value("테스트 댓글입니다."))
                .andExpect(jsonPath("$.data[0].writer").value("testNickname"));

        then(feedCommentService).should().getComments(feedId);
    }

    @Test
    @DisplayName("피드 댓글을 삭제할 수 있다")
    void deleteComment_Success() throws Exception {
        // given
        Long feedId = 1L;
        Long commentId = 1L;
        Long memberId = 1L;

        doNothing().when(feedCommentService).delete(commentId, memberId);

        // when & then
        mockMvc.perform(delete("/api/v1/feeds/{feedId}/comments/{commentId}", feedId, commentId)
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        then(feedCommentService).should().delete(commentId, memberId);
    }

    @Test
    @DisplayName("대댓글을 생성할 수 있다")
    void createReplyComment_Success() throws Exception {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        Long parentId = 2L;
        
        String requestJson = """
                {
                    "content": "대댓글입니다.",
                    "parentId": %d
                }
                """.formatted(parentId);

        doNothing().when(feedCommentService).save(any(CommentCreateRequest.class), eq(feedId), eq(memberId));

        // when & then
        mockMvc.perform(post("/api/v1/feeds/{feedId}/comments", feedId)
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        then(feedCommentService).should().save(any(CommentCreateRequest.class), eq(feedId), eq(memberId));
    }

    @Test
    @DisplayName("빈 댓글 내용으로 생성 시 검증 에러가 발생한다")
    void createComment_EmptyContent_BadRequest() throws Exception {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        
        String requestJson = """
                {
                    "content": "",
                    "parentId": null
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/feeds/{feedId}/comments", feedId)
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 내용이 너무 긴 경우 검증 에러가 발생한다")
    void createComment_ContentTooLong_BadRequest() throws Exception {
        // given
        Long feedId = 1L;
        Long memberId = 1L;
        String longContent = "a".repeat(10001); // 10000자 초과
        
        String requestJson = """
                {
                    "content": "%s",
                    "parentId": null
                }
                """.formatted(longContent);

        // when & then
        mockMvc.perform(post("/api/v1/feeds/{feedId}/comments", feedId)
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("계층 구조를 가진 댓글 목록을 조회할 수 있다")
    void getComments_WithHierarchy_Success() throws Exception {
        // given
        Long feedId = 1L;
        
        CommentResponse childComment = CommentResponse.builder()
                .id(2L)
                .content("자식 댓글입니다.")
                .writer("childUser")
                .writerId(2L)
                .parentId(1L)
                .createdAt(LocalDateTime.now())
                .children(Collections.emptyList())
                .build();

        CommentResponse parentComment = CommentResponse.builder()
                .id(1L)
                .content("부모 댓글입니다.")
                .writer("parentUser")
                .writerId(1L)
                .parentId(null)
                .createdAt(LocalDateTime.now())
                .children(List.of(childComment))
                .build();

        List<CommentResponse> comments = List.of(parentComment);
        given(feedCommentService.getComments(feedId)).willReturn(comments);

        // when & then
        mockMvc.perform(get("/api/v1/feeds/{feedId}/comments", feedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].content").value("부모 댓글입니다."))
                .andExpect(jsonPath("$.data[0].children").isArray())
                .andExpect(jsonPath("$.data[0].children[0].id").value(2L))
                .andExpect(jsonPath("$.data[0].children[0].content").value("자식 댓글입니다."));

        then(feedCommentService).should().getComments(feedId);
    }

    @Test
    @DisplayName("멤버 ID 없이 댓글 생성 시 인증 에러가 발생한다")
    void createComment_WithoutMemberId_Unauthorized() throws Exception {
        // given
        Long feedId = 1L;
        
        String requestJson = """
                {
                    "content": "테스트 댓글입니다.",
                    "parentId": null
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/feeds/{feedId}/comments", feedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("멤버 ID 없이 댓글 삭제 시 인증 에러가 발생한다")
    void deleteComment_WithoutMemberId_Unauthorized() throws Exception {
        // given
        Long feedId = 1L;
        Long commentId = 1L;

        // when & then
        mockMvc.perform(delete("/api/v1/feeds/{feedId}/comments/{commentId}", feedId, commentId))
                .andExpect(status().isUnauthorized());
    }
}
