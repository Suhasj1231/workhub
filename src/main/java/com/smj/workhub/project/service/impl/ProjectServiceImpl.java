package com.smj.workhub.project.service.impl;

import com.smj.workhub.common.exception.DuplicateResourceException;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.project.entity.Project;
import com.smj.workhub.project.repository.ProjectRepository;
import com.smj.workhub.project.service.ProjectService;
import com.smj.workhub.project.specification.ProjectSpecification;
import com.smj.workhub.workspace.entity.Workspace;
import com.smj.workhub.workspace.repository.WorkspaceRepository;
import com.smj.workhub.activity.service.ActivityService;
import com.smj.workhub.activity.entity.ActivityAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import com.smj.workhub.security.principal.UserPrincipal;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ActivityService activityService;

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            WorkspaceRepository workspaceRepository,
            ActivityService activityService
    ) {
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
        this.activityService = activityService;
    }

    // -------- CREATE --------

    @Override
    public Project createProject(Long workspaceId, String name, String description) {
        log.info("Creating project name={} in workspaceId={}", name, workspaceId);

        Workspace workspace = workspaceRepository
                .findByIdAndDeletedFalse(workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workspace not found with id: " + workspaceId
                        )
                );

        String normalizedName = name.trim();
        if (projectRepository.existsByWorkspaceIdAndNameAndDeletedFalse(workspaceId, normalizedName)) {
            throw new DuplicateResourceException(
                    "Project with name '" + normalizedName + "' already exists in this workspace"
            );
        }

        Project project = new Project(workspace, normalizedName, description);

        Project saved = projectRepository.save(project);

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.PROJECT_CREATED,
                workspaceId,
                saved.getId(),
                null,
                "Project '" + saved.getName() + "' created",
                null
        );

        log.info("Project created successfully id={} workspaceId={}", saved.getId(), workspaceId);
        return saved;
    }

    // -------- GET BY ID --------

    @Override
    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        log.debug("Fetching project id={}", id);
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
        log.debug("Fetching projects for workspaceId={} search={} includeDeleted={}", workspaceId, search, includeDeleted);

        Specification<Project> specification =
                ProjectSpecification.build(workspaceId, search, includeDeleted);

        return projectRepository.findAll(specification, pageable);
    }

    // -------- UPDATE --------

    @Override
    public Project updateProject(Long id, String name, String description) {
        log.info("Updating project id={}", id);

        Project project = projectRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );

        String oldName = project.getName();
        String oldDescription = project.getDescription();

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

        String metadata = String.format(
                "{\"oldName\":\"%s\",\"newName\":\"%s\",\"oldDescription\":\"%s\",\"newDescription\":\"%s\"}",
                oldName,
                project.getName(),
                oldDescription,
                project.getDescription()
        );

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.PROJECT_UPDATED,
                project.getWorkspace().getId(),
                project.getId(),
                null,
                "Project '" + oldName + "' updated",
                metadata
        );

        log.info("Project updated successfully id={}", project.getId());
        return project;
    }

    // -------- SOFT DELETE --------

    @Override
    public void deleteProject(Long id) {
        log.warn("Soft deleting project id={}", id);

        Project project = projectRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );

        project.setDeleted(true);
        projectRepository.save(project);

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.PROJECT_DELETED,
                project.getWorkspace().getId(),
                project.getId(),
                null,
                "Project '" + project.getName() + "' soft deleted",
                null
        );
    }

    // -------- RESTORE --------

    @Override
    public Project restoreProject(Long id) {
        log.info("Restoring project id={}", id);

        Project project = projectRepository.findByIdAndDeletedTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Deleted project not found with id: " + id
                        )
                );

        project.setDeleted(false);
        Project restored = projectRepository.save(project);

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.PROJECT_RESTORED,
                project.getWorkspace().getId(),
                project.getId(),
                null,
                "Project '" + project.getName() + "' restored from deleted state",
                null
        );

        log.info("Project restored successfully id={}", project.getId());
        return restored;
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new RuntimeException("User not authenticated");
    }
}
