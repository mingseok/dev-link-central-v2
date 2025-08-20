package dev.devlink.member.controller.view;

import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import dev.devlink.common.jwt.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(MemberViewController.class)
class MemberViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private AuthMemberIdArgumentResolver authMemberIdArgumentResolver;

    @Test
    @DisplayName("회원가입 폼을 보여준다")
    void showSignUpForm_ReturnsSignUpView() throws Exception {
        // when & then
        mockMvc.perform(get("/view/members/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("/members/sign-up"));
    }

    @Test
    @DisplayName("로그인 폼을 보여준다")
    void showSignInForm_ReturnsSignInView() throws Exception {
        // when & then
        mockMvc.perform(get("/view/members/signin"))
                .andExpect(status().isOk())
                .andExpect(view().name("/members/sign-in"));
    }

    @Test
    @DisplayName("홈 페이지를 보여준다")
    void showHomePage_ReturnsHomeView() throws Exception {
        // when & then
        mockMvc.perform(get("/view/members/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("/members/home"));
    }
}
