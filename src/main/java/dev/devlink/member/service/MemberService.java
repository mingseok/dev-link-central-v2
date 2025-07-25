package dev.devlink.member.service;

import dev.devlink.common.jwt.JwtToken;
import dev.devlink.common.jwt.JwtTokenProvider;
import dev.devlink.member.entity.Member;
import dev.devlink.member.exception.MemberError;
import dev.devlink.member.exception.MemberException;
import dev.devlink.member.repository.MemberRepository;
import dev.devlink.member.service.dto.request.SignInRequest;
import dev.devlink.member.service.dto.request.SignUpRequest;
import dev.devlink.member.service.dto.response.JwtTokenResponse;
import dev.devlink.member.service.dto.response.NicknameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new MemberException(MemberError.EMAIL_DUPLICATED);
        }

        if (memberRepository.existsByNickname(signUpRequest.getNickname())) {
            throw new MemberException(MemberError.NICKNAME_DUPLICATED);
        }

        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        Member member = Member.create(
                signUpRequest.getName(),
                signUpRequest.getEmail(),
                signUpRequest.getNickname(),
                encodedPassword
        );
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public NicknameResponse findNicknameById(Long memberId) {
        String nickname = memberRepository.findNicknameById(memberId)
                .orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));
        return NicknameResponse.from(nickname);
    }

    @Transactional(readOnly = true)
    public JwtTokenResponse signin(SignInRequest signInRequest) {
        Member member = findByEmail(signInRequest.getEmail());
        if (!passwordEncoder.matches(signInRequest.getPassword(), member.getPassword())) {
            throw new MemberException(MemberError.PASSWORD_NOT_MATCHED);
        }

        JwtToken token = jwtTokenProvider.generateToken(member.getId());
        return JwtTokenResponse.from(token);
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));
    }

    private Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberError.EMAIL_NOT_FOUND));
    }
}
