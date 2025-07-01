package dev.devlink.article.controller.open;

import dev.devlink.article.controller.response.ArticleListResponse;
import dev.devlink.article.service.ArticleService;
import dev.devlink.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/articles")
public class ArticlePublicController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ArticleListResponse>>> getPagedArticles(
            @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ArticleListResponse> articlePage = articleService.findArticlesByPage(pageable);
        return ResponseEntity.ok(ApiResponse.success(articlePage));
    }
}
