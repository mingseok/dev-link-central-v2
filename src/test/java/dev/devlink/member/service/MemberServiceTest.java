package dev.devlink.member.service;

import dev.devlink.common.util.PasswordUtil;
import dev.devlink.member.controller.request.SignUpRequest;
import dev.devlink.member.controller.response.SignUpResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUp_Success() {
        // given
        SignUpRequest signUpRequest = createSignUpRequest();
        String encodedPassword = "encodedPassword123";
        
        Member savedMember = Member.builder()
                .name("minseok")
                .password(encodedPassword)
                .email("minseok@example.com")
                .nickname("testnick")
                .build();
        
        given(passwordUtil.encode("password123")).willReturn(encodedPassword);
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        SignUpResponse response = memberService.signUp(signUpRequest);

        // then
        assertNotNull(response);

        verify(passwordUtil).encode("password123");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("패스워드 인코딩 확인 테스트")
    void signUp_PasswordEncoding() {
        // given
        SignUpRequest signUpRequest = createSignUpRequest();
        Member savedMember = createMockMember("encodedPassword123");

        given(passwordUtil.encode("password123")).willReturn("encodedPassword123");
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        memberService.signUp(signUpRequest);

        // then
        verify(passwordUtil).encode("password123");
        verify(memberRepository).save(argThat(member ->
                member.getPassword().equals("encodedPassword123")
        ));
    }

    @Test
    @DisplayName("회원 정보가 정확히 저장되는지 테스트")
    void signUp_MemberDataSaving() {
        // given
        SignUpRequest signUpRequest = createSignUpRequest();
        Member savedMember = createMockMember("encodedPassword123");

        given(passwordUtil.encode("password123")).willReturn("encodedPassword123");
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        memberService.signUp(signUpRequest);

        // then
        verify(memberRepository).save(argThat(member ->
                member.getName().equals("minseok") &&
                        member.getEmail().equals("minseok@example.com") &&
                        member.getNickname().equals("testnick") &&
                        member.getPassword().equals("encodedPassword123")
        ));
    }

    private Member createMockMember(String encodedPassword) {
        return Member.builder()
                .name("minseok")
                .password(encodedPassword)
                .email("minseok@example.com")
                .nickname("testnick")
                .build();
    }

    private SignUpRequest createSignUpRequest() {
        return new SignUpRequest(
                "minseok",
                "password123", 
                "minseok@example.com",
                "testnick"
        );
    }
}
