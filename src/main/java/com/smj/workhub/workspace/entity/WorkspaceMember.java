package com.smj.workhub.workspace.entity;

import com.smj.workhub.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "workspace_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workspace_user",
                        columnNames = {"workspace_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_workspace_member_workspace", columnList = "workspace_id"),
                @Index(name = "idx_workspace_member_user", columnList = "user_id"),
                @Index(name = "idx_workspace_member_deleted", columnList = "is_deleted")
        }
)
public class WorkspaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---- Workspace relationship ----
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    // ---- User relationship ----
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ---- Role inside workspace ----
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkspaceRole role;

    // ---- Soft delete support ----
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // ---- Audit field ----
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    // ---- Lifecycle hook ----
    @PrePersist
    protected void onCreate() {
        this.joinedAt = Instant.now();
    }

    // ---- Getters ----

    public Long getId() {
        return id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public User getUser() {
        return user;
    }

    public WorkspaceRole getRole() {
        return role;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    // ---- Setters ----

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRole(WorkspaceRole role) {
        this.role = role;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}