package dev.devlink.article.service;

import dev.devlink.article.constant.LikeStatus;
import dev.devlink.article.entity.Article;
import dev.devlink.article.entity.ArticleLike;
import dev.devlink.article.exception.ArticleError;
import dev.devlink.article.exception.ArticleException;
import dev.devlink.article.repository.ArticleLikeRepository;
import dev.devlink.article.repository.ArticleRepository;
import dev.devlink.common.redis.RedisKey;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {

    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final RedissonClient redissonClient;
    private final MemberService memberService;

    private static final long LOCK_WAIT_TIME = 3L;
    private static final long LOCK_LEASE_TIME = 1L;

    @Transactional
    public LikeStatus updateLikeStatus(Long articleId, Long memberId) {
        String lockKey = RedisKey.articleLikeLockKey(articleId, memberId);
        RLock lock = redissonClient.getLock(lockKey);

        boolean lockAcquired = false;
        try {
            lockAcquired = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!lockAcquired) {
                throw new ArticleException(ArticleError.CONCURRENT_LIKE_REQUEST);
            }
            return addOrRemoveLike(articleId, memberId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ArticleException(ArticleError.LOCK_INTERRUPTED);
        } finally {
            if (lockAcquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private LikeStatus addOrRemoveLike(Long articleId, Long memberId) {
        Member member = memberService.findMemberById(memberId);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));

        Optional<ArticleLike> likeOptional = articleLikeRepository.findByArticleAndMember(article, member);
        if (likeOptional.isPresent()) {
            articleLikeRepository.delete(likeOptional.get());
            return LikeStatus.LIKE_REMOVED;
        }

        articleLikeRepository.save(ArticleLike.create(article, member));
        return LikeStatus.LIKE_ADDED;
    }

    @Transactional(readOnly = true)
    public long countLikes(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleError.ARTICLE_NOT_FOUND));
        return articleLikeRepository.countByArticle(article);
    }
}
