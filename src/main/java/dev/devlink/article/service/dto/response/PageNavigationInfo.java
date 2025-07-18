package dev.devlink.article.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageNavigationInfo {

    private final int startPage;
    private final int endPage;
}
