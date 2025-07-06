package dev.devlink.member.service;

import dev.devlink.common.jwt.JwtToken;
import dev.devlink.common.jwt.JwtTokenProvider;
import dev.devlink.member.controller.response.JwtTokenResponse;
import dev.devlink.member.controller.response.NicknameResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.exception.MemberError;
import dev.devlink.member.exception.MemberException;
import dev.devlink.member.repository.MemberRepository;
import dev.devlink.member.service.command.SignInCommand;
import dev.devlink.member.service.command.SignUpCommand;
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
    public void signUp(SignUpCommand command) {
        if (memberRepository.existsByEmail(command.getEmail())) {
            throw new MemberException(MemberError.EMAIL_DUPLICATED);
        }

        if (memberRepository.existsByNickname(command.getNickname())) {
            throw new MemberException(MemberError.NICKNAME_DUPLICATED);
        }

        String encodedPassword = passwordEncoder.encode(command.getPassword());
        Member member = Member.create(
                command.getName(),
                command.getEmail(),
                command.getNickname(),
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
    public JwtTokenResponse signin(SignInCommand command) {
        Member member = findByEmail(command.getEmail());
        if (!passwordEncoder.matches(command.getPassword(), member.getPassword())) {
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
