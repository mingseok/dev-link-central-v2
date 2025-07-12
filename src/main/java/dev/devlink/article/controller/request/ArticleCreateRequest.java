package dev.devlink.article.controller.request;

import dev.devlink.article.service.command.ArticleCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreateRequest {

    @NotBlank(message = "게시글 제목은 필수 입력 항목입니다.")
    @Size(min = 3, max = 100, message = "게시글 제목은 3자 이상 100자 이하이어야 합니다.")
    private String title;

    @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
    @Size(min = 3, max = 10000, message = "게시글 내용은 3자 이상 10000자 이하이어야 합니다.")
    private String content;

    public ArticleCreateCommand toCommand(Long memberId) {
        return ArticleCreateCommand.builder()
                .title(this.title)
                .content(this.content)
                .memberId(memberId)
                .build();
    }
}
