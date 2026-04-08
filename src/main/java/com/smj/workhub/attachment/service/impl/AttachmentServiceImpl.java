package com.smj.workhub.attachment.service.impl;

import com.smj.workhub.activity.entity.ActivityAction;
import com.smj.workhub.activity.service.ActivityService;
import com.smj.workhub.attachment.dto.AttachmentResponse;
import com.smj.workhub.attachment.entity.Attachment;
import com.smj.workhub.attachment.repository.AttachmentRepository;
import com.smj.workhub.attachment.service.AttachmentService;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.security.principal.UserPrincipal;
import com.smj.workhub.workspace.service.WorkspaceAccessService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private static final String UPLOAD_DIR = "uploads";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_TYPES = {"application/pdf", "image/png", "image/jpeg"};

    private final AttachmentRepository attachmentRepository;
    private final WorkspaceAccessService workspaceAccessService;
    private final ActivityService activityService;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository,
                                 WorkspaceAccessService workspaceAccessService,
                                 ActivityService activityService) {
        this.attachmentRepository = attachmentRepository;
        this.workspaceAccessService = workspaceAccessService;
        this.activityService = activityService;
    }

    @Override
    public AttachmentResponse uploadAttachment(Long taskId, MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        Long userId = getCurrentUserId();

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds limit (5MB)");
        }

        // Validate file type
        String contentType = file.getContentType();
        boolean allowed = false;
        for (String type : ALLOWED_TYPES) {
            if (type.equals(contentType)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new IllegalArgumentException("Invalid file type");
        }

        // Verify access (task-level access via workspace)
        workspaceAccessService.verifyTaskAccess(taskId);

        try {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.contains("..")) {
                throw new IllegalArgumentException("Invalid file name");
            }
            String storedFileName = UUID.randomUUID() + "_" + originalFileName;

            Path uploadPath = Paths.get(UPLOAD_DIR, String.valueOf(taskId));
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(storedFileName);
            Files.write(filePath, file.getBytes());

            Attachment attachment = new Attachment();
            attachment.setTaskId(taskId);
            attachment.setFileName(originalFileName);
            attachment.setStoredFileName(storedFileName);
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setFilePath(filePath.toString());
            attachment.setUploadedBy(userId);

            Attachment saved = attachmentRepository.save(attachment);

            activityService.logActivity(
                    userId,
                    ActivityAction.ATTACHMENT_UPLOADED,
                    null,
                    null,
                    taskId,
                    "Uploaded file: " + originalFileName,
                    null
            );

            return mapToResponse(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttachmentResponse> getAttachmentsByTask(Long taskId, Pageable pageable) {
        workspaceAccessService.verifyTaskAccess(taskId);
        return attachmentRepository.findByTaskIdAndDeletedFalse(taskId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse getAttachmentMetadata(Long attachmentId) {
        Attachment attachment = attachmentRepository.findByIdAndDeletedFalse(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

        workspaceAccessService.verifyTaskAccess(attachment.getTaskId());

        return mapToResponse(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findByIdAndDeletedFalse(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

        workspaceAccessService.verifyTaskAccess(attachment.getTaskId());

        try {
            return Files.readAllBytes(Paths.get(attachment.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    @Override
    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findByIdAndDeletedFalse(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

        workspaceAccessService.verifyTaskAccess(attachment.getTaskId());

        Long userId = getCurrentUserId();

        if (!attachment.getUploadedBy().equals(userId)) {
            throw new IllegalArgumentException("Only uploader can delete attachment");
        }

        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }

        attachment.setDeleted(true);
        attachmentRepository.save(attachment);

        activityService.logActivity(
                userId,
                ActivityAction.ATTACHMENT_DELETED,
                null,
                null,
                attachment.getTaskId(),
                "Deleted file: " + attachment.getFileName(),
                null
        );
    }

    private AttachmentResponse mapToResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .taskId(attachment.getTaskId())
                .fileName(attachment.getFileName())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .uploadedBy(attachment.getUploadedBy())
                .createdAt(attachment.getCreatedAt())
                .build();
    }

    private Long getCurrentUserId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return principal.getId();
    }
}
