package dev.devlink.member.service;

import dev.devlink.common.jwt.JwtToken;
import dev.devlink.common.jwt.JwtTokenProvider;
import dev.devlink.member.controller.response.JwtTokenResponse;
import dev.devlink.member.controller.response.NicknameResponse;
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
    public void signUp(String name, String email, String nickname, String password) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(MemberError.EMAIL_DUPLICATED);
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new MemberException(MemberError.NICKNAME_DUPLICATED);
        }

        String encodedPassword = passwordEncoder.encode(password);
        Member member = Member.create(name, email, nickname, encodedPassword);
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public NicknameResponse findNicknameById(Long memberId) {
        String nickname = memberRepository.findNicknameById(memberId)
                .orElseThrow(() -> new MemberException(MemberError.MEMBER_NOT_FOUND));
        return NicknameResponse.from(nickname);
    }

    @Transactional(readOnly = true)
    public JwtTokenResponse signin(String email, String password) {
        Member member = findByEmail(email);
        if (!passwordEncoder.matches(password, member.getPassword())) {
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
