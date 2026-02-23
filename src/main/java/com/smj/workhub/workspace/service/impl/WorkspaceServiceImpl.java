package com.smj.workhub.workspace.service.impl;

import com.smj.workhub.common.exception.DuplicateResourceException;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.workspace.entity.Workspace;
import com.smj.workhub.workspace.repository.WorkspaceRepository;
import com.smj.workhub.workspace.service.WorkspaceService;
import com.smj.workhub.workspace.specification.WorkspaceSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    // -------- WRITE OPERATIONS --------

    @Override
    @Transactional
    public Workspace createWorkspace(String name, String description) {

        String normalizedName = name.trim();

        if (workspaceRepository.existsByNameAndDeletedFalse(normalizedName)) {
            throw new DuplicateResourceException(
                    "Workspace with name '" + normalizedName + "' already exists"
            );
        }

        Workspace workspace = new Workspace();
        workspace.setName(normalizedName);
        workspace.setDescription(description);

        return workspaceRepository.save(workspace);
    }

    @Override
    @Transactional
    public Workspace updateWorkspace(Long id, String name, String description) {

        Workspace workspace = getWorkspaceById(id);

        String normalizedName = name.trim();

        if (!workspace.getName().equals(normalizedName)
                && workspaceRepository.existsByNameAndDeletedFalse(normalizedName)) {
            throw new DuplicateResourceException(
                    "Workspace with name '" + normalizedName + "' already exists"
            );
        }

        workspace.setName(normalizedName);
        workspace.setDescription(description);

        // No save() needed — dirty checking will persist
        return workspace;
    }

    @Transactional
    @Override
    public void deleteWorkspace(Long id) {
        Workspace workspace = workspaceRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workspace not found with id: " + id
                        )
                );

        if (workspace.isDeleted()) {
            return; // idempotent delete
        }

        workspace.setDeleted(true);
        workspace.setDeletedAt(Instant.now());
    }


    // -------- READ OPERATIONS --------

    @Override
    @Transactional(readOnly = true)
    public Workspace getWorkspaceById(Long id) {
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


}

