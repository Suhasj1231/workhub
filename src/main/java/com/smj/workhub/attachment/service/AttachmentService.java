package com.smj.workhub.attachment.service;

import com.smj.workhub.attachment.dto.AttachmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {

    // Upload file to a task
    AttachmentResponse uploadAttachment(Long taskId, MultipartFile file);

    // Get all attachments for a task
    Page<AttachmentResponse> getAttachmentsByTask(Long taskId, Pageable pageable);

    // Download file (returns file bytes + metadata handled in controller)
    AttachmentResponse getAttachmentMetadata(Long attachmentId);

    byte[] downloadAttachment(Long attachmentId);

    // Delete attachment (soft delete + physical delete)
    void deleteAttachment(Long attachmentId);
}
