package dev.devlink.profile.service;

import dev.devlink.common.file.FileConstants;
import dev.devlink.common.file.FileUploadService;
import dev.devlink.follow.service.FollowService;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberService memberService;
    private final FollowService followService;
    private final ProfileRepository profileRepository;
    private final FileUploadService fileUploadService;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long viewerId, Long targetId) {
        Member target = memberService.findMemberById(targetId);

        boolean isFollowingExists = followService.isFollowing(viewerId, targetId);
        long followers = followService.getFollowerCount(targetId);
        long followings = followService.getFollowingCount(targetId);

        Profile profile = profileRepository.findByMember(target).orElse(null);
        String imageUrl = FileConstants.DEFAULT_IMAGE_URL;

        String bio = "";
        if (profile != null) {
            bio = profile.getBio();

            if (profile.getImageUrl() != null) {
                imageUrl = profile.getImageUrl();
            }
        }

        return ProfileResponse.from(
                target, bio, imageUrl, isFollowingExists, followers, followings);
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

    @Transactional
    public String updateImage(Long memberId, MultipartFile file) {
        Member member = memberService.findMemberById(memberId);
        Profile profile = profileRepository.findByMember(member)
                .orElseGet(() -> profileRepository.save(
                        Profile.create(member, ProfileDefaults.DEFAULT_BIO)
                ));

        if (profile.getImageUrl() != null) {
            fileUploadService.deleteFile(profile.getImageUrl());
        }

        String imageUrl = fileUploadService.uploadFile(file, FileConstants.PROFILE);
        profile.updateImage(imageUrl);
        return imageUrl;
    }

    @Transactional(readOnly = true)
    public String getProfileImageUrl(Long memberId) {
        Profile profile = profileRepository.findByMemberId(memberId).orElse(null);

        if (profile == null || profile.getImageUrl() == null) {
            return FileConstants.DEFAULT_IMAGE_URL;
        }
        return profile.getImageUrl();
    }
}
