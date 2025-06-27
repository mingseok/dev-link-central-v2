package dev.devlink.article.comment.repository;

import dev.devlink.article.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    boolean existsByParentId(Long parentId);

    List<Comment> findAllByArticleId(Long articleId);
}
