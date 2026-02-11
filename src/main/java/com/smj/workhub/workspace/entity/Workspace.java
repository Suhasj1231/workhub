package com.smj.workhub.workspace.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "workspaces",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workspace_name",
                        columnNames = "name"
                )
        },
        indexes = {
                @Index(
                        name = "idx_workspace_is_deleted",
                        columnList = "is_deleted"
                )
        }
)
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    // ---- Soft Delete Fields ----

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column
    private Instant deletedAt;

    // ---- lifecycle hooks ----

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ---- getters & setters ----

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
