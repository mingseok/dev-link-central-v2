package dev.devlink.comment.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    private Long parentId;

    @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
    @Size(min = 1, max = 10000, message = "댓글 내용은 1자 이상 10000자 이하이어야 합니다.")
    private String content;
}
