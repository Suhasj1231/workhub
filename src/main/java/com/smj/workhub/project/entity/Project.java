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
                        name = "uk_projects_workspace_name",
                        columnNames = {"workspace_id", "name"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_projects_workspace_deleted_created",
                        columnList = "workspace_id, deleted, created_at"
                )
        }
)
public class Project {

    // -------- PRIMARY KEY --------

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

    // -------- AUDIT FIELDS --------

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // -------- CONSTRUCTORS --------

    protected Project() {
        // Required by JPA
    }

    public Project(Workspace workspace, String name, String description) {
        this.workspace = workspace;
        this.name = name;
        this.description = description;
    }

    // -------- GETTERS --------

    public Long getId() {
        return id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // -------- SETTERS --------

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}