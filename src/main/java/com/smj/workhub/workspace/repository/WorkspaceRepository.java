package com.smj.workhub.workspace.repository;

import com.smj.workhub.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WorkspaceRepository
        extends JpaRepository<Workspace, Long>,
        JpaSpecificationExecutor<Workspace> {

    // Find workspace by name
    Optional<Workspace> findByName(String name);

    // Find active workspace by ID (soft delete aware)
    Optional<Workspace> findByIdAndDeletedFalse(Long id);

    // Check duplicate name among active workspaces
    boolean existsByNameAndDeletedFalse(String name);

}
