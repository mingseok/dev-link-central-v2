package dev.devlink.member.service;

import dev.devlink.common.util.PasswordUtil;
import dev.devlink.member.controller.request.SignUpRequest;
import dev.devlink.member.controller.response.SignUpResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.exception.MemberError;
import dev.devlink.member.exception.MemberException;
import dev.devlink.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordUtil passwordUtil;

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        String encodedPassword = passwordUtil.encode(signUpRequest.getPassword());
        Member member = signUpRequest.toEntity(encodedPassword);
        Member savedMember = memberRepository.save(member);
        return SignUpResponse.from(savedMember.getId());
    }

    @Transactional(readOnly = true)
    public Long signin(String email, String password) {
        Member member = findByEmail(email);
        if (!passwordUtil.matches(password, member.getPasswordHash())) {
            throw new MemberException(MemberError.PASSWORD_NOT_MATCHED);
        }
        return member.getId();
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new MemberException(MemberError.EMAIL_NOT_FOUND));
    }
}
