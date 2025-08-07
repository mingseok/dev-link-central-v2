package dev.devlink.profile.service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequest {

    @Size(max = 255, message = "소개글은 255자 이하로 입력해주세요.")
    private String bio;
}
