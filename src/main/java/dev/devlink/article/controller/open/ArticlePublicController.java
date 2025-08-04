package dev.devlink.article.controller.open;

import dev.devlink.article.service.ArticleLikeService;
import dev.devlink.article.service.ArticleRankingService;
import dev.devlink.article.service.ArticleService;
import dev.devlink.article.service.dto.response.ArticleListResponse;
import dev.devlink.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/articles")
public class ArticlePublicController {

    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final ArticleRankingService articleRankingService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ArticleListResponse>>> getPagedArticles(
            @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ArticleListResponse> articlePage = articleService.findArticlesByPage(pageable);
        return ResponseEntity.ok(ApiResponse.success(articlePage));
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<ApiResponse<Long>> countLikes(
            @PathVariable Long id
    ) {
        long count = articleLikeService.countLikes(id);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/best")
    public ResponseEntity<ApiResponse<List<ArticleListResponse>>> findTopRankedArticles() {
        List<ArticleListResponse> response = articleRankingService.getTopRankedArticles();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
