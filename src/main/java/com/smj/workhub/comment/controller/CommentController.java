package com.smj.workhub.comment.controller;

import com.smj.workhub.comment.dto.CommentResponse;
import com.smj.workhub.comment.dto.CreateCommentRequest;
import com.smj.workhub.comment.dto.UpdateCommentRequest;
import com.smj.workhub.comment.service.CommentService;
import com.smj.workhub.common.error.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Create comment or reply on a task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment created"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiError.class)))
    })
    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return ResponseEntity.ok(commentService.createComment(taskId, request));
    }

    @Operation(summary = "Get comments of a task (paginated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments fetched"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiError.class)))
    })
    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long taskId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId, pageable));
    }

    @Operation(summary = "Update a comment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment updated"),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiError.class)))
    })
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }

    @Operation(summary = "Delete a comment (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment deleted"),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
