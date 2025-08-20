package dev.devlink.member.service;

import dev.devlink.common.jwt.JwtToken;
import dev.devlink.common.jwt.JwtTokenProvider;
import dev.devlink.member.entity.Member;
import dev.devlink.member.exception.MemberException;
import dev.devlink.member.repository.MemberRepository;
import dev.devlink.member.service.dto.request.SignInRequest;
import dev.devlink.member.service.dto.request.SignUpRequest;
import dev.devlink.member.service.dto.response.JwtTokenResponse;
import dev.devlink.member.service.dto.response.NicknameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private SignUpRequest signUpRequest;
    private Member testMember;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest(
                "김민석",
                "password123",
                "hong@example.com", 
                "김민석닉네임"
        );
        
        testMember = Member.create(
                "김민석",
                "hong@example.com",
                "김민석닉네임", 
                "encodedPassword"
        );
    }

    @Test
    @DisplayName("회원가입이 성공적으로 처리된다")
    void signUp_Success() {
        // given
        given(memberRepository.existsByEmail(signUpRequest.getEmail())).willReturn(false);
        given(memberRepository.existsByNickname(signUpRequest.getNickname())).willReturn(false);
        given(passwordEncoder.encode(signUpRequest.getPassword())).willReturn("encodedPassword");

        // when
        memberService.signUp(signUpRequest);

        // then
        then(memberRepository).should().save(any(Member.class));
    }

    @Test
    @DisplayName("이메일이 중복되면 회원가입이 실패한다")
    void signUp_EmailDuplicated_ThrowsException() {
        // given
        given(memberRepository.existsByEmail(signUpRequest.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.signUp(signUpRequest))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("닉네임이 중복되면 회원가입이 실패한다")
    void signUp_NicknameDuplicated_ThrowsException() {
        // given
        given(memberRepository.existsByEmail(signUpRequest.getEmail())).willReturn(false);
        given(memberRepository.existsByNickname(signUpRequest.getNickname())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.signUp(signUpRequest))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("회원 ID로 닉네임을 조회할 수 있다")
    void findNicknameById_Success() {
        // given
        Long memberId = 1L;
        String nickname = "김민석닉네임";
        given(memberRepository.findNicknameById(memberId)).willReturn(Optional.of(nickname));

        // when
        NicknameResponse response = memberService.findNicknameById(memberId);

        // then
        assertThat(response.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 조회하면 예외가 발생한다")
    void findNicknameById_MemberNotFound_ThrowsException() {
        // given
        Long memberId = 999L;
        given(memberRepository.findNicknameById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.findNicknameById(memberId))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("로그인이 성공적으로 처리된다")
    void signin_Success() {
        // given
        SignInRequest signInRequest = new SignInRequest("hong@example.com", "password123");
        JwtToken jwtToken = JwtToken.builder()
                .grantType("Bearer")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
        
        given(memberRepository.findByEmail(signInRequest.getEmail())).willReturn(Optional.of(testMember));
        given(passwordEncoder.matches(signInRequest.getPassword(), testMember.getPassword())).willReturn(true);
        given(jwtTokenProvider.generateToken(testMember.getId())).willReturn(jwtToken);

        // when
        JwtTokenResponse response = memberService.signin(signInRequest);

        // then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인하면 예외가 발생한다")
    void signin_EmailNotFound_ThrowsException() {
        // given
        SignInRequest signInRequest = new SignInRequest("notfound@example.com", "password123");
        given(memberRepository.findByEmail(signInRequest.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.signin(signInRequest))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 로그인이 실패한다")
    void signin_PasswordNotMatched_ThrowsException() {
        // given
        SignInRequest signInRequest = new SignInRequest("hong@example.com", "wrongPassword");
        given(memberRepository.findByEmail(signInRequest.getEmail())).willReturn(Optional.of(testMember));
        given(passwordEncoder.matches(signInRequest.getPassword(), testMember.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.signin(signInRequest))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("회원 ID로 회원을 조회할 수 있다")
    void findMemberById_Success() {
        // given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));

        // when
        Member foundMember = memberService.findMemberById(memberId);

        // then
        assertThat(foundMember).isEqualTo(testMember);
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 조회하면 예외가 발생한다")
    void findMemberById_NotFound_ThrowsException() {
        // given
        Long memberId = 999L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.findMemberById(memberId))
                .isInstanceOf(MemberException.class);
    }
}
