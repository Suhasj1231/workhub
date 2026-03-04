package com.smj.workhub.project.service.impl;

import com.smj.workhub.common.exception.DuplicateResourceException;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.project.entity.Project;
import com.smj.workhub.project.repository.ProjectRepository;
import com.smj.workhub.project.service.ProjectService;
import com.smj.workhub.project.specification.ProjectSpecification;
import com.smj.workhub.workspace.entity.Workspace;
import com.smj.workhub.workspace.repository.WorkspaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            WorkspaceRepository workspaceRepository
    ) {
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
    }

    // -------- CREATE --------

    @Override
    public Project createProject(Long workspaceId, String name, String description) {

        Workspace workspace = workspaceRepository
                .findByIdAndDeletedFalse(workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workspace not found with id: " + workspaceId
                        )
                );

        if (projectRepository.existsByWorkspaceIdAndNameAndDeletedFalse(workspaceId, name)) {
            throw new DuplicateResourceException(
                    "Project with name '" + name + "' already exists in this workspace"
            );
        }

        Project project = new Project(workspace, name.trim(), description);

        return projectRepository.save(project);
    }

    // -------- GET BY ID --------

    @Override
    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        return projectRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );
    }

    // -------- LIST (FILTER + PAGINATION) --------

    @Override
    @Transactional(readOnly = true)
    public Page<Project> getProjects(
            Long workspaceId,
            String search,
            Boolean includeDeleted,
            Pageable pageable
    ) {

        Specification<Project> specification =
                ProjectSpecification.build(workspaceId, search, includeDeleted);

        return projectRepository.findAll(specification, pageable);
    }

    // -------- UPDATE --------

    @Override
    public Project updateProject(Long id, String name, String description) {

        Project project = projectRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );

        String normalizedName = name.trim();

        boolean duplicateExists =
                projectRepository.existsByWorkspaceIdAndNameAndDeletedFalse(
                        project.getWorkspace().getId(),
                        normalizedName
                );

        if (duplicateExists && !project.getName().equals(normalizedName)) {
            throw new DuplicateResourceException(
                    "Project with name '" + normalizedName + "' already exists in this workspace"
            );
        }

        project.setName(normalizedName);
        project.setDescription(description);

        return project;
    }

    // -------- SOFT DELETE --------

    @Override
    public void deleteProject(Long id) {

        Project project = projectRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );

        project.setDeleted(true);
    }

    // -------- RESTORE --------

    @Override
    public Project restoreProject(Long id) {

        Project project = projectRepository.findByIdAndDeletedTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Deleted project not found with id: " + id
                        )
                );

        project.setDeleted(false);

        return project;
    }
}
