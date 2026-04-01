package com.smj.workhub.workspace.service.impl;

import com.smj.workhub.common.exception.DuplicateResourceException;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.workspace.entity.Workspace;
import com.smj.workhub.workspace.entity.WorkspaceMember;
import com.smj.workhub.workspace.entity.WorkspaceRole;
import com.smj.workhub.workspace.repository.WorkspaceRepository;
import com.smj.workhub.workspace.repository.WorkspaceMemberRepository;
import com.smj.workhub.workspace.service.WorkspaceService;
import com.smj.workhub.workspace.specification.WorkspaceSpecification;
import com.smj.workhub.user.entity.User;
import com.smj.workhub.user.repository.UserRepository;
import com.smj.workhub.activity.service.ActivityService;
import com.smj.workhub.activity.entity.ActivityAction;
import com.smj.workhub.security.principal.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceServiceImpl.class);

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    public WorkspaceServiceImpl(
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository workspaceMemberRepository,
            UserRepository userRepository,
            ActivityService activityService
    ) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    // -------- WRITE OPERATIONS --------

    @Override
    @Transactional
    public Workspace createWorkspace(String name, String description) {
        log.info("Creating workspace with name={}", name);

        String normalizedName = name.trim();

        if (workspaceRepository.existsByNameAndDeletedFalse(normalizedName)) {
            throw new DuplicateResourceException(
                    "Workspace with name '" + normalizedName + "' already exists"
            );
        }

        Workspace workspace = new Workspace();
        workspace.setName(normalizedName);
        workspace.setDescription(description);

        Workspace saved = workspaceRepository.save(workspace);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        WorkspaceMember membership = new WorkspaceMember();
        membership.setWorkspace(saved);
        membership.setUser(user);
        membership.setRole(WorkspaceRole.OWNER);

        workspaceMemberRepository.save(membership);

        Long userId = user.getId();
        String metadata = String.format(
                "{\"description\":\"%s\"}",
                saved.getDescription()
        );

        activityService.logActivity(
                userId,
                ActivityAction.WORKSPACE_CREATED,
                saved.getId(),
                null,
                null,
                "Workspace '" + saved.getName() + "' created",
                metadata
        );

        log.info("Workspace created successfully with id={} and owner={}", saved.getId(), user.getId());

        return saved;
    }

    @Override
    @Transactional
    public Workspace updateWorkspace(Long id, String name, String description) {
        log.info("Updating workspace id={}", id);

        Workspace workspace = getWorkspaceById(id);

        String oldName = workspace.getName();
        String oldDescription = workspace.getDescription();

        String normalizedName = name.trim();

        if (!workspace.getName().equals(normalizedName)
                && workspaceRepository.existsByNameAndDeletedFalse(normalizedName)) {
            throw new DuplicateResourceException(
                    "Workspace with name '" + normalizedName + "' already exists"
            );
        }

        workspace.setName(normalizedName);
        workspace.setDescription(description);

        String metadata = String.format(
                "{\"oldName\":\"%s\",\"newName\":\"%s\",\"oldDescription\":\"%s\",\"newDescription\":\"%s\"}",
                oldName,
                workspace.getName(),
                oldDescription,
                workspace.getDescription()
        );

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.WORKSPACE_UPDATED,
                workspace.getId(),
                null,
                null,
                "Workspace '" + workspace.getName() + "' updated",
                metadata
        );

        log.info("Workspace updated successfully id={}", workspace.getId());
        // No save() needed — dirty checking will persist
        return workspace;
    }

    @Transactional
    @Override
    public void deleteWorkspace(Long id) {
        log.warn("Deleting workspace id={}", id);

        Workspace workspace = workspaceRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workspace not found with id: " + id
                        )
                );

        if (workspace.isDeleted()) {
            return; // idempotent delete
        }

        log.info("Soft deleting workspace id={}", workspace.getId());
        workspace.setDeleted(true);
        workspace.setDeletedAt(Instant.now());

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.WORKSPACE_DELETED,
                workspace.getId(),
                null,
                null,
                "Workspace '" + workspace.getName() + "' soft deleted",
                null
        );
    }


    // -------- READ OPERATIONS --------

    @Override
    @Transactional(readOnly = true)
    public Workspace getWorkspaceById(Long id) {
        log.debug("Fetching workspace id={}", id);
        return workspaceRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workspace not found with id: " + id
                        )
                );
    }



    @Override
    @Transactional(readOnly = true)
    public Page<Workspace> getAllWorkspaces(
            Boolean deleted,
            String name,
            Pageable pageable
    ) {
        log.debug("Fetching workspaces with filters deleted={}, name={}", deleted, name);

        Specification<Workspace> spec = Specification.where(null);

        // Apply deleted filter (if provided)
        if (deleted != null) {
            spec = spec.and(
                    WorkspaceSpecification.hasDeleted(deleted)
            );
        }

        // Apply name filter (if provided)
        if (name != null && !name.isBlank()) {
            spec = spec.and(
                    WorkspaceSpecification.nameContains(name)
            );
        }

        return workspaceRepository.findAll(spec, pageable);
    }

    // Restoring the workspace
    @Override
    @Transactional
    public Workspace restoreWorkspace(Long id) {
        log.info("Restoring workspace id={}", id);

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workspace not found with id: " + id
                        )
                );

        if (!workspace.isDeleted()) {
            throw new IllegalStateException(
                    "Workspace is already active"
            );
        }

        // Check name conflict with active workspaces
        boolean nameExists = workspaceRepository
                .existsByNameAndDeletedFalse(workspace.getName());

        if (nameExists) {
            throw new DuplicateResourceException(
                    "Active workspace with same name already exists"
            );
        }

        workspace.setDeleted(false);
        workspace.setDeletedAt(null);

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.WORKSPACE_RESTORED,
                workspace.getId(),
                null,
                null,
                "Workspace '" + workspace.getName() + "' restored from deleted state",
                null
        );

        log.info("Workspace restored successfully id={}", workspace.getId());

        return workspace;
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new RuntimeException("User not authenticated");
    }

}
