package com.smj.workhub.project.entity;

import com.smj.workhub.workspace.entity.Workspace;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "projects",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workspace_project_name",
                        columnNames = {"workspace_id", "name"}
                )
        },
        indexes = {
                @Index(name = "idx_project_workspace_id", columnList = "workspace_id"),
                @Index(name = "idx_project_deleted", columnList = "deleted"),
                @Index(name = "idx_project_created_at", columnList = "created_at")
        }
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------- RELATIONSHIP --------

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "workspace_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_project_workspace")
    )
    private Workspace workspace;

    // -------- BUSINESS FIELDS --------

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    // -------- SOFT DELETE --------

    @Column(nullable = false)
    private boolean deleted = false;

    // -------- AUDIT --------

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // -------- GETTERS / SETTERS --------
}