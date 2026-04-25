package com.smj.workhub.comment.service;

import com.smj.workhub.comment.dto.CommentResponse;
import com.smj.workhub.comment.dto.CreateCommentRequest;
import com.smj.workhub.comment.dto.UpdateCommentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponse createComment(Long taskId, CreateCommentRequest request);

    Page<CommentResponse> getCommentsByTask(Long taskId, Pageable pageable);

    CommentResponse updateComment(Long commentId, UpdateCommentRequest request);

    void deleteComment(Long commentId);
}
