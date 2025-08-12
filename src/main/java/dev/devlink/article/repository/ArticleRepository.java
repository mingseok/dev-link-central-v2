package dev.devlink.article.repository;

import dev.devlink.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a JOIN FETCH a.member")
    Page<Article> findAllWithMember(Pageable pageable);

    @Query("SELECT a FROM Article a JOIN FETCH a.member WHERE a.id = :id")
    Optional<Article> findDetailById(@Param("id") Long id);

    @Query("SELECT a FROM Article a ORDER BY a.viewCount DESC")
    List<Article> findTopByViews(Pageable pageable);
}
