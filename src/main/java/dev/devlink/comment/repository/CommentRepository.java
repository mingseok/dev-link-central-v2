package dev.devlink.comment.repository;

import dev.devlink.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    boolean existsByParentId(Long parentId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.article.id = :articleId")
    List<Comment> findAllByArticleId(@Param("articleId") Long articleId);
}
