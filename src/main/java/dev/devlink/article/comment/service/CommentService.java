package dev.devlink.article.comment.service;

import dev.devlink.article.comment.controller.request.CommentCreateRequest;
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
    public void save(Long articleId, Long memberId, CommentCreateRequest request) {
        Article article = articleService.findArticleById(articleId);
        Member member = memberService.findMemberById(memberId);

        Comment parent = findParentOrNull(request.getParentId());

        Comment comment = Comment.create(article, member, request.getContent(), parent);
        commentRepository.save(comment);
    }

    private Comment findParentOrNull(Long parentId) {
        if (parentId == null) {
            return null; // 최상위 댓글
        }
        return commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentException(CommentError.PARENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentTreeByArticleId(Long articleId) {
        List<Comment> comments = commentRepository.findAllByArticleId(articleId);

        Map<Long, CommentResponse> responseMap = new HashMap<>();
        List<CommentResponse> rootComments = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponse response = CommentResponse.from(comment);
            responseMap.put(comment.getId(), response);

            Long parentId = comment.getParentIdOrNull();
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
    public void delete(Long commentId, Long currentMemberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentError.NOT_FOUND));

        validateWriterId(comment, currentMemberId);
        validateHasNoChildComments(commentId);

        commentRepository.delete(comment);
    }

    private void validateWriterId(Comment comment, Long currentMemberId) {
        if (!comment.getWriterId().equals(currentMemberId)) {
            throw new CommentException(CommentError.NO_PERMISSION);
        }
    }

    private void validateHasNoChildComments(Long commentId) {
        if (commentRepository.existsByParentId(commentId)) {
            throw new CommentException(CommentError.HAS_CHILD_COMMENTS);
        }
    }
}
