package dev.devlink.follow.service;

import dev.devlink.follow.entity.Follow;
import dev.devlink.follow.exception.FollowError;
import dev.devlink.follow.exception.FollowException;
import dev.devlink.follow.repository.FollowRepository;
import dev.devlink.follow.service.dto.request.FollowCreateRequest;
import dev.devlink.follow.service.dto.response.FollowResponse;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final MemberService memberService;
    private final FollowRepository followRepository;

    @Transactional
    public void follow(Long followerId, FollowCreateRequest request) {
        if (followerId.equals(request.getFolloweeId())) {
            throw new FollowException(FollowError.CANNOT_FOLLOW_SELF);
        }

        Member follower = memberService.findMemberById(followerId);
        Member followee = memberService.findMemberById(request.getFolloweeId());

        followRepository.findByFollowerAndFollowee(follower, followee)
                .ifPresent(f -> { throw new FollowException(FollowError.ALREADY_FOLLOWING); });

        Follow follow = Follow.create(follower, followee);
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long followerId, Long followeeId) {
        Member follower = memberService.findMemberById(followerId);
        Member followee = memberService.findMemberById(followeeId);

        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new FollowException(FollowError.NOT_FOUND));

        followRepository.delete(follow);
    }

    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowers(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        List<Follow> followers = followRepository.findAllByFollowee(member);
        
        List<FollowResponse> result = new ArrayList<>();
        for (Follow follow : followers) {
            Member follower = follow.getFollower();
            boolean isFollowing = followRepository.existsByFollowerAndFollowee(member, follower);
            FollowResponse response = FollowResponse.from(follower, isFollowing);
            result.add(response);
        }
        
        return result;
    }

    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowings(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        return followRepository.findAllByFollower(member).stream()
                .map(follow -> FollowResponse.from(follow.getFollowee()))
                .toList();
    }
}
