package dev.devlink.member.controller.open;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import dev.devlink.common.jwt.TokenProvider;
import dev.devlink.member.service.MemberService;
import dev.devlink.member.service.dto.request.SignInRequest;
import dev.devlink.member.service.dto.request.SignUpRequest;
import dev.devlink.member.service.dto.response.JwtTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberPublicController.class)
class MemberPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private AuthMemberIdArgumentResolver authMemberIdArgumentResolver;

    @Test
    @DisplayName("회원가입이 성공적으로 처리된다")
    void signup_Success() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest(
                "김민석",
                "password123",
                "hong@example.com",
                "김민석닉네임"
        );

        willDoNothing().given(memberService).signUp(any(SignUpRequest.class));

        // when & then
        mockMvc.perform(post("/api/public/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("로그인이 성공적으로 처리된다")
    void signin_Success() throws Exception {
        // given
        SignInRequest request = new SignInRequest(
                "hong@example.com",
                "password123"
        );

        JwtTokenResponse response = new JwtTokenResponse(
                "accessToken",
                "refreshToken"
        );

        given(memberService.signin(any(SignInRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/public/members/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"));
    }

    @Test
    @DisplayName("잘못된 요청으로 회원가입하면 400 에러가 발생한다")
    void signup_InvalidRequest_BadRequest() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest(
                "",         // 빈 이름
                "",               // 빈 비밀번호
                "invalid-email",  // 잘못된 이메일 형식
                ""                // 빈 닉네임
        );

        // when & then
        mockMvc.perform(post("/api/public/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 요청으로 로그인하면 400 에러가 발생한다")
    void signin_InvalidRequest_BadRequest() throws Exception {
        // given
        SignInRequest request = new SignInRequest(
                "",  // 빈 이메일
                ""         // 빈 비밀번호
        );

        // when & then
        mockMvc.perform(post("/api/public/members/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
