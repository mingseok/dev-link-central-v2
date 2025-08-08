package dev.devlink.comment.service;

import dev.devlink.comment.service.dto.request.CommentCreateRequest;
import dev.devlink.comment.service.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    
    void save(CommentCreateRequest request, Long targetId, Long memberId);
    
    List<CommentResponse> getComments(Long targetId);
    
    void delete(Long commentId, Long memberId);
}
