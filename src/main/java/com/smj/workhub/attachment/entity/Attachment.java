package com.smj.workhub.attachment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "attachments",
        indexes = {
                @Index(name = "idx_attachment_task", columnList = "task_id"),
                @Index(name = "idx_attachment_uploaded_by", columnList = "uploaded_by"),
                @Index(name = "idx_attachment_deleted", columnList = "deleted"),
                @Index(name = "idx_attachment_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 RELATION (Task reference via ID)
    @Column(name = "task_id", nullable = false)
    private Long taskId;

    // 📄 ORIGINAL FILE NAME (user uploaded)
    @Column(name = "file_name", nullable = false)
    private String fileName;

    // 🧠 STORED FILE NAME (UUID based)
    @Column(name = "stored_file_name", nullable = false, unique = true)
    private String storedFileName;

    // 📦 FILE TYPE (MIME)
    @Column(name = "file_type", nullable = false)
    private String fileType;

    // 📏 FILE SIZE (in bytes)
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    // 📁 FILE PATH (disk location)
    @Column(name = "file_path", nullable = false)
    private String filePath;

    // 👤 WHO UPLOADED
    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;

    // 🗑 SOFT DELETE
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    // ⏱ CREATED TIME
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}