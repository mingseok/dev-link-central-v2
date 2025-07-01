package dev.devlink.member.service;

import dev.devlink.common.jwt.JwtToken;
import dev.devlink.common.jwt.JwtTokenProvider;
import dev.devlink.member.controller.request.SignInRequest;
import dev.devlink.member.controller.request.SignUpRequest;
import dev.devlink.member.controller.response.JwtTokenResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.exception.MemberError;
import dev.devlink.member.exception.MemberException;
import dev.devlink.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordUtil;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUp_Success() {
        // given
        SignUpRequest signUpRequest = createSignUpRequest();
        String encodedPassword = "encodedPassword123";
        
        Member savedMember = Member.builder()
                .name("김민석")
                .password(encodedPassword)
                .email("minseok@naver.com")
                .nickname("석석석")
                .build();
        
        given(passwordUtil.encode("password123")).willReturn(encodedPassword);
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        memberService.signUp(signUpRequest);

        // then
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

    private Member createMockMember(String encodedPassword) {
        return Member.builder()
                .name("김민석")
                .password(encodedPassword)
                .email("minseok@naver.com")
                .nickname("석석석")
                .build();
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
                member.getName().equals("김민석") &&
                        member.getEmail().equals("minseok@naver.com") &&
                        member.getNickname().equals("석석석") &&
                        member.getPassword().equals("encodedPassword123")
        ));
    }

    private SignUpRequest createSignUpRequest() {
        return new SignUpRequest(
                "김민석",
                "password123",
                "minseok@naver.com",
                "석석석"
        );
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void signin_Success() {
        // given
        SignInRequest request = new SignInRequest("minseok@naver.com", "password123");
        Member member = createMockMember("encodedPassword123");

        given(memberRepository.findByEmailAndDeletedFalse(request.getEmail())).willReturn(Optional.of(member));
        given(passwordUtil.matches(request.getPassword(), "encodedPassword123")).willReturn(true);

        JwtToken mockToken = new JwtToken("Bearer", "access-token-value", "refresh-token-value");
        given(jwtTokenProvider.generateToken(member.getId())).willReturn(mockToken);

        // when
        JwtTokenResponse tokenResponse = memberService.signin(request);

        // then
        assertNotNull(tokenResponse);
        assertEquals("access-token-value", tokenResponse.getAccessToken());
        assertEquals("refresh-token-value", tokenResponse.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void signin_PasswordMismatch() {
        // given
        SignInRequest request = new SignInRequest("minseok@naver.com", "wrongPassword");
        Member member = createMockMember("encodedPassword123");

        given(memberRepository.findByEmailAndDeletedFalse(request.getEmail())).willReturn(Optional.of(member));
        given(passwordUtil.matches(request.getPassword(), "encodedPassword123")).willReturn(false);

        // when & then
        MemberException exception = assertThrows(MemberException.class, () -> {
            memberService.signin(request);
        });

        assertEquals(MemberError.PASSWORD_NOT_MATCHED, exception.getCommonError());
    }

}
