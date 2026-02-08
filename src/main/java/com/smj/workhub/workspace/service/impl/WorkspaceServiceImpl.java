package com.smj.workhub.workspace.service.impl;

import com.smj.workhub.common.exception.DuplicateResourceException;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.workspace.entity.Workspace;
import com.smj.workhub.workspace.repository.WorkspaceRepository;
import com.smj.workhub.workspace.service.WorkspaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (workspaceRepository.existsByName(normalizedName)) {
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
                && workspaceRepository.existsByName(normalizedName)) {
            throw new DuplicateResourceException(
                    "Workspace with name '" + normalizedName + "' already exists"
            );
        }

        workspace.setName(normalizedName);
        workspace.setDescription(description);

        // No save() needed — dirty checking will persist
        return workspace;
    }

    @Override
    @Transactional
    public void deleteWorkspace(Long id) {
        Workspace workspace = getWorkspaceById(id);
        workspaceRepository.delete(workspace);
    }

    // -------- READ OPERATIONS --------

    @Override
    @Transactional(readOnly = true)
    public Workspace getWorkspaceById(Long id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workspace not found with id: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Workspace> getAllWorkspaces() {
        return workspaceRepository.findAll();
    }
}

