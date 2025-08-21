package dev.devlink.article.controller.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ArticleViewControllerTest {

    @InjectMocks
    private ArticleViewController articleViewController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(articleViewController)
                .build();
    }

    @Test
    @DisplayName("게시글 작성 폼을 보여준다")
    void showSaveForm_Success() throws Exception {
        mockMvc.perform(get("/view/articles/save"))
                .andExpect(status().isOk())
                .andExpect(view().name("articles/save"));
    }

    @Test
    @DisplayName("게시글 상세 페이지를 보여준다")
    void showDetailPage_Success() throws Exception {
        mockMvc.perform(get("/view/articles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("articles/detail"));
    }

    @Test
    @DisplayName("게시글 목록 페이지를 보여준다")
    void showPagedArticles_Success() throws Exception {
        mockMvc.perform(get("/view/articles/paging"))
                .andExpect(status().isOk())
                .andExpect(view().name("/articles/paging"));
    }

    @Test
    @DisplayName("게시글 수정 폼을 보여준다")
    void showUpdateForm_Success() throws Exception {
        mockMvc.perform(get("/view/articles/update/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("/articles/update"));
    }
}
