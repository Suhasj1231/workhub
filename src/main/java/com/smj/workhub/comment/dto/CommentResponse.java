package com.smj.workhub.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CommentResponse {

    private Long id;

    private Long taskId;

    private Long userId;

    private String content;

    private Long parentCommentId;

    private Instant createdAt;

    private Instant updatedAt;
}
