package dev.devlink.comment.repository;

import dev.devlink.comment.entity.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
    
    boolean existsByParentId(Long parentId);

    @Query("SELECT fc FROM FeedComment fc JOIN FETCH fc.member WHERE fc.feed.id = :feedId")
    List<FeedComment> findAllByFeedId(@Param("feedId") Long feedId);

    long countByFeedId(Long feedId);
}
