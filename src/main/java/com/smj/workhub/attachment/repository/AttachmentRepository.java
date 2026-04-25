package com.smj.workhub.attachment.repository;

import com.smj.workhub.attachment.entity.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    // Get attachment by id (only active)
    Optional<Attachment> findByIdAndDeletedFalse(Long id);

    // List attachments for a task (only active)
    Page<Attachment> findByTaskIdAndDeletedFalse(Long taskId, Pageable pageable);

    // Optional: list all attachments uploaded by a user
    Page<Attachment> findByUploadedByAndDeletedFalse(Long uploadedBy, Pageable pageable);
}
