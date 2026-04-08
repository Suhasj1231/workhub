package com.smj.workhub.attachment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class AttachmentResponse {

    private Long id;

    private Long taskId;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private Long uploadedBy;

    private Instant createdAt;
}
