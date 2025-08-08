package dev.devlink.feed.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedCreateRequest {

    @NotBlank(message = "피드 내용은 필수입니다.")
    @Size(max = 1000, message = "피드 내용은 1000자를 초과할 수 없습니다.")
    private String content;
}
