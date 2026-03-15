package com.smj.workhub.workspace.repository;

import com.smj.workhub.workspace.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {

    // Check if a user belongs to a workspace
    boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    // Get membership of a user inside a workspace
    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    // List all members of a workspace
    List<WorkspaceMember> findByWorkspaceId(Long workspaceId);

    // List all workspaces a user belongs to
    List<WorkspaceMember> findByUserId(Long userId);

    Optional<WorkspaceMember> findByWorkspaceIdAndUserIdAndDeletedFalse(
            Long workspaceId,
            Long userId
    );
}
