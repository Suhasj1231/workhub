package com.smj.workhub.project.service;

import com.smj.workhub.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    Project createProject(Long workspaceId, String name, String description);

    Project getProjectById(Long id);

    Page<Project> getProjects(
            Long workspaceId,
            String search,
            Boolean includeDeleted,
            Pageable pageable
    );

    Project updateProject(Long id, String name, String description);

    void deleteProject(Long id);

    Project restoreProject(Long id);
}