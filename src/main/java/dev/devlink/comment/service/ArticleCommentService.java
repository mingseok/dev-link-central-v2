package dev.devlink.comment.service;

import dev.devlink.article.entity.Article;
import dev.devlink.article.service.ArticleService;
import dev.devlink.comment.entity.ArticleComment;
import dev.devlink.comment.exception.CommentError;
import dev.devlink.comment.exception.CommentException;
import dev.devlink.comment.repository.ArticleCommentRepository;
import dev.devlink.comment.service.dto.request.CommentCreateRequest;
import dev.devlink.comment.service.dto.response.CommentResponse;
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
public class ArticleCommentService implements CommentService {

    private final MemberService memberService;
    private final ArticleService articleService;
    private final ArticleCommentRepository commentRepository;

    @Override
    @Transactional
    public void save(CommentCreateRequest request, Long articleId, Long memberId) {
        Member member = memberService.findMemberById(memberId);
        Article article = articleService.findArticleById(articleId);
        
        validateParentComment(request.getParentId());
        ArticleComment comment = ArticleComment.create(
                article, member, request.getParentId(), request.getContent());

        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long articleId) {
        articleService.findArticleById(articleId);
        List<ArticleComment> comments = commentRepository.findAllByArticleId(articleId);
        return buildCommentTree(comments);
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long memberId) {
        ArticleComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentError.NOT_FOUND));
        
        comment.checkAuthor(memberId);
        if (commentRepository.existsByParentId(commentId)) {
            throw new CommentException(CommentError.HAS_CHILD_COMMENTS);
        }
        commentRepository.delete(comment);
    }

    private void validateParentComment(Long parentId) {
        if (parentId == null) {
            return;
        }

        boolean parentExists = commentRepository.existsById(parentId);
        if (!parentExists) {
            throw new CommentException(CommentError.PARENT_NOT_FOUND);
        }
    }

    private List<CommentResponse> buildCommentTree(List<ArticleComment> comments) {
        List<CommentResponse> rootComments = new ArrayList<>();
        Map<Long, CommentResponse> responseMap = new HashMap<>();

        for (ArticleComment comment : comments) {
            CommentResponse response = CommentResponse.from(comment);
            responseMap.put(comment.getId(), response);
        }

        for (ArticleComment comment : comments) {
            CommentResponse response = responseMap.get(comment.getId());
            Long parentId = comment.getParentId();
            
            if (parentId == null) {
                rootComments.add(response);
            } else {
                CommentResponse parent = responseMap.get(parentId);
                if (parent != null) {
                    parent.addChild(response);
                }
            }
        }

        return rootComments;
    }
}
