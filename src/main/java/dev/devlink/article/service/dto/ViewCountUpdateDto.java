package dev.devlink.article.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ViewCountUpdateDto {

    private final Long articleId;
    private final Long viewCount;
}
