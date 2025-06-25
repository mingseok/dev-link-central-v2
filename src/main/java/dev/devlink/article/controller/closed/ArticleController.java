package dev.devlink.article.controller.closed;

import dev.devlink.article.controller.request.ArticleCreateRequest;
import dev.devlink.article.controller.request.ArticleUpdateRequest;
import dev.devlink.article.service.ArticleService;
import dev.devlink.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
            HttpServletRequest httpRequest
    ) {
        Long memberId = (Long) httpRequest.getAttribute("memberId");
        articleService.save(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successEmpty());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long id,
            @Validated @RequestBody ArticleUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        Long memberId = (Long) httpRequest.getAttribute("memberId");
        articleService.update(id, request, memberId);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long memberId = (Long) request.getAttribute("memberId");
        articleService.delete(id, memberId);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }
}
