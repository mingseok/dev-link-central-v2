package dev.devlink.profile.service;

import dev.devlink.follow.repository.FollowRepository;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import dev.devlink.profile.constant.ProfileDefaults;
import dev.devlink.profile.entity.Profile;
import dev.devlink.profile.repository.ProfileRepository;
import dev.devlink.profile.service.dto.request.ProfileUpdateRequest;
import dev.devlink.profile.service.dto.response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberService memberService;
    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long viewerId, Long targetId) {
        Member viewer = memberService.findMemberById(viewerId);
        Member target = memberService.findMemberById(targetId);

        boolean isFollowingExists = followRepository.existsByFollowerAndFollowee(viewer, target);

        long followers = followRepository.countByFollowee(target);
        long followings = followRepository.countByFollower(target);

        String bio = profileRepository.findByMember(target)
                .map(Profile::getBio)
                .orElse("");

        return ProfileResponse.from(target, bio, isFollowingExists, followers, followings);
    }

    @Transactional
    public void updateBio(Long memberId, ProfileUpdateRequest request) {
        Member member = memberService.findMemberById(memberId);
        Profile profile = profileRepository.findByMember(member)
                .orElseGet(() -> profileRepository.save(
                        Profile.create(member, ProfileDefaults.DEFAULT_BIO)
                ));

        profile.updateBio(request.getBio());
    }
}
