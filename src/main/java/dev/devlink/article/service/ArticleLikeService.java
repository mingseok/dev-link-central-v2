package dev.devlink.article.service;

import dev.devlink.article.constant.LikeStatus;
import dev.devlink.article.entity.Article;
import dev.devlink.article.entity.ArticleLike;
import dev.devlink.article.exception.ArticleError;
import dev.devlink.article.exception.ArticleException;
import dev.devlink.article.repository.ArticleLikeRepository;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    public LikeStatus likeOrCancel(Long articleId, Long memberId) {
        Member member = memberService.findMemberById(memberId);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));

        Optional<ArticleLike> likeOptional = articleLikeRepository.findByArticleAndMember(article, member);
        if (likeOptional.isPresent()) {
            articleLikeRepository.delete(likeOptional.get());
            return LikeStatus.LIKE_REMOVED;
        }

        try {
            articleLikeRepository.save(ArticleLike.create(article, member));
            return LikeStatus.LIKE_ADDED;
        } catch (DataIntegrityViolationException e) {
            articleLikeRepository.deleteByArticleAndMember(article, member);
            return LikeStatus.LIKE_REMOVED;
        }
    }

    @Transactional(readOnly = true)
    public long countLikes(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));
        return articleLikeRepository.countByArticle(article);
    }
}
