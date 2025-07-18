package dev.devlink.article.controller.closed;

import dev.devlink.article.controller.request.ArticleCreateRequest;
import dev.devlink.article.controller.request.ArticleUpdateRequest;
import dev.devlink.article.service.ArticleService;
import dev.devlink.article.service.dto.response.ArticleDetailResponse;
import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.identity.annotation.AuthMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(
            @Validated @RequestBody ArticleCreateRequest request,
            @AuthMemberId Long memberId
    ) {
        articleService.save(request.toServiceDto(memberId));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successEmpty());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @Validated @RequestBody ArticleUpdateRequest request,
            @PathVariable Long id,
            @AuthMemberId Long memberId
    ) {
        articleService.update(request.toServiceDto(id, memberId));
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthMemberId Long memberId
    ) {
        articleService.delete(id, memberId);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDetailResponse>> findDetail(
            @PathVariable Long id,
            @AuthMemberId Long memberId
    ) {
        ArticleDetailResponse response = articleService.findDetail(id, memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
