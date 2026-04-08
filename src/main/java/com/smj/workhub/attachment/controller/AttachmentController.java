package com.smj.workhub.attachment.controller;

import com.smj.workhub.attachment.dto.AttachmentResponse;
import com.smj.workhub.attachment.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Attachments", description = "Attachment management APIs")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @Operation(summary = "Upload attachment to a task")
    @PostMapping(value = "/tasks/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentResponse uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file
    ) {
        return attachmentService.uploadAttachment(taskId, file);
    }

    @Operation(summary = "Get all attachments for a task")
    @GetMapping("/tasks/{taskId}/attachments")
    public Page<AttachmentResponse> getAttachmentsByTask(
            @PathVariable Long taskId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return attachmentService.getAttachmentsByTask(taskId, pageable);
    }

    @Operation(summary = "Download attachment")
    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long attachmentId) {

        AttachmentResponse metadata = attachmentService.getAttachmentMetadata(attachmentId);
        byte[] data = attachmentService.downloadAttachment(attachmentId);

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + metadata.getFileName() + "\"")
                .contentLength(data.length)
                .body(resource);
    }

    @Operation(summary = "Delete attachment")
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
