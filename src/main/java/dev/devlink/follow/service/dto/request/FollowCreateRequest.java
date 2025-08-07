package dev.devlink.follow.service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowCreateRequest {

    @NotNull(message = "팔로우할 대상 ID는 필수입니다.")
    private Long followeeId;
}
