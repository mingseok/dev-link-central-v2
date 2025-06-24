package dev.devlink.member.service;

import dev.devlink.common.jwt.JwtToken;
import dev.devlink.common.jwt.JwtTokenProvider;
import dev.devlink.member.controller.request.SignInRequest;
import dev.devlink.member.controller.request.SignUpRequest;
import dev.devlink.member.controller.response.JwtTokenResponse;
import dev.devlink.member.controller.response.SignUpResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.exception.MemberError;
import dev.devlink.member.exception.MemberException;
import dev.devlink.member.repository.MemberRepository;
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
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        validateDuplicateMember(signUpRequest);
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        Member member = signUpRequest.toEntity(encodedPassword);
        Member savedMember = memberRepository.save(member);
        return SignUpResponse.from(savedMember.getId());
    }

    @Transactional(readOnly = true)
    public JwtTokenResponse signin(SignInRequest request) {
        Member member = findByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), member.getPasswordHash())) {
            throw new MemberException(MemberError.PASSWORD_NOT_MATCHED);
        }

        JwtToken token = jwtTokenProvider.generateToken(member.getId());
        return JwtTokenResponse.from(token);
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new MemberException(MemberError.EMAIL_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public void validateDuplicateMember(SignUpRequest signUpRequest) {
        if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new MemberException(MemberError.EMAIL_DUPLICATED);
        }

        if (memberRepository.existsByNickname(signUpRequest.getNickname())) {
            throw new MemberException(MemberError.NICKNAME_DUPLICATED);
        }
    }
}
