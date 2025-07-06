package dev.devlink.article.comment.service;

import dev.devlink.article.comment.controller.response.CommentResponse;
import dev.devlink.article.comment.entity.Comment;
import dev.devlink.article.comment.exception.CommentError;
import dev.devlink.article.comment.exception.CommentException;
import dev.devlink.article.comment.repository.CommentRepository;
import dev.devlink.article.entity.Article;
import dev.devlink.article.service.ArticleService;
import dev.devlink.member.entity.Member;
import dev.devlink.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberService memberService;
    private final ArticleService articleService;
    private final CommentRepository commentRepository;

    @Transactional
    public void save(Long parentId, String content, Long articleId, Long memberId) {
        Article article = articleService.findArticleById(articleId);
        Member member = memberService.findMemberById(memberId);

        validateParent(parentId);
        Comment comment = Comment.create(article, member, content, parentId);

        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentTreeByArticleId(Long articleId) {
        List<Comment> comments = commentRepository.findAllByArticleId(articleId);

        Map<Long, CommentResponse> responseMap = new HashMap<>();
        List<CommentResponse> rootComments = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponse response = CommentResponse.from(comment);
            responseMap.put(comment.getId(), response);

            Long parentId = comment.getParentId();
            if (parentId == null) {
                rootComments.add(response);
                continue;
            }

            CommentResponse parent = responseMap.get(parentId);
            if (parent != null) {
                parent.addChild(response);
            }
        }
        return rootComments;
    }

    @Transactional
    public void delete(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentError.NOT_FOUND));

        comment.checkAuthor(memberId);
        validateDeletable(commentId);

        commentRepository.delete(comment);
    }

    private void validateParent(Long parentId) {
        if (parentId == null) {
            return;
        }

        boolean parentExists = commentRepository.existsById(parentId);
        if (!parentExists) {
            throw new CommentException(CommentError.PARENT_NOT_FOUND);
        }
    }

    private void validateDeletable(Long commentId) {
        if (commentRepository.existsByParentId(commentId)) {
            throw new CommentException(CommentError.HAS_CHILD_COMMENTS);
        }
    }
}
