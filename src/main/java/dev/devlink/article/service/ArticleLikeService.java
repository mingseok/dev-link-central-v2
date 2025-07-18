package dev.devlink.article.service;

import dev.devlink.article.entity.Article;
import dev.devlink.article.entity.ArticleLike;
import dev.devlink.article.exception.ArticleError;
import dev.devlink.article.exception.ArticleException;
import dev.devlink.article.repository.ArticleLikeRepository;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {

    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final MemberService memberService;

    @Transactional
    public boolean toggleLike(Long articleId, Long memberId) {
        Member member = memberService.findMemberById(memberId);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));

        Optional<ArticleLike> existingLike = articleLikeRepository
                .findByArticleAndMember(article, member);

        if (existingLike.isPresent()) {
            articleLikeRepository.delete(existingLike.get());
            return false;
        }

        addLike(article, member);
        return true;
    }

    private void addLike(Article article, Member member) {
        ArticleLike newLike = ArticleLike.create(article, member);
        articleLikeRepository.save(newLike);
    }

    @Transactional(readOnly = true)
    public long countLikes(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));
        return articleLikeRepository.countByArticle(article);
    }
}
