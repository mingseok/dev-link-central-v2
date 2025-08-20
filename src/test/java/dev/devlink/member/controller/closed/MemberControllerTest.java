package dev.devlink.member.controller.closed;

import dev.devlink.common.identity.resolver.AuthMemberIdArgumentResolver;
import dev.devlink.member.service.MemberService;
import dev.devlink.member.service.dto.response.NicknameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setCustomArgumentResolvers(new AuthMemberIdArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("인증된 사용자의 닉네임을 조회할 수 있다")
    void findCurrentNickname_Success() throws Exception {
        // given
        Long memberId = 1L;
        String nickname = "김민석닉네임";
        NicknameResponse response = new NicknameResponse(nickname);

        given(memberService.findNicknameById(memberId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/members/self")
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.nickname").value(nickname));
    }
}
