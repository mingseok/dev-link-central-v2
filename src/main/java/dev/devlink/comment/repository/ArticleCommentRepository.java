package dev.devlink.comment.repository;

import dev.devlink.comment.entity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    
    boolean existsByParentId(Long parentId);

    @Query("SELECT ac FROM ArticleComment ac JOIN FETCH ac.member WHERE ac.article.id = :articleId")
    List<ArticleComment> findAllByArticleId(@Param("articleId") Long articleId);
}
