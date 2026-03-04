package com.smj.workhub.project.repository;

import com.smj.workhub.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProjectRepository
        extends JpaRepository<Project, Long>,
        JpaSpecificationExecutor<Project> {

    // -------- FIND BY ID (ACTIVE ONLY) --------

    Optional<Project> findByIdAndDeletedFalse(Long id);

    // -------- DUPLICATE CHECK (WITHIN WORKSPACE) --------

    boolean existsByWorkspaceIdAndNameAndDeletedFalse(Long workspaceId, String name);

    // -------- RESTORE SUPPORT --------

    Optional<Project> findByIdAndDeletedTrue(Long id);


}