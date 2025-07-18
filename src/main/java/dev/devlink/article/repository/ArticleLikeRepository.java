package dev.devlink.article.repository;

import dev.devlink.article.entity.Article;
import dev.devlink.article.entity.ArticleLike;
import dev.devlink.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    long countByArticle(Article article);

    Optional<ArticleLike> findByArticleAndMember(Article article, Member member);
}
