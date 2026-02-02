package com.smj.workhub.workspace.service.impl;

import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.workspace.entity.Workspace;
import com.smj.workhub.workspace.repository.WorkspaceRepository;
import com.smj.workhub.workspace.service.WorkspaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public Workspace createWorkspace(String name, String description) {

        if (workspaceRepository.existsByName(name)) {
            throw new IllegalArgumentException("Workspace with name already exists");
        }

        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setDescription(description);

        return workspaceRepository.save(workspace);
    }

    @Override
    public Workspace getWorkspaceById(Long id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workspace not found with id: " + id
                        )
                );
    }




    @Override
    public List<Workspace> getAllWorkspaces() {
        return workspaceRepository.findAll();
    }

    @Override
    public Workspace updateWorkspace(Long id, String name, String description) {

        Workspace workspace = getWorkspaceById(id);

        if (!workspace.getName().equals(name)
                && workspaceRepository.existsByName(name)) {
            throw new IllegalArgumentException("Workspace with name already exists");
        }

        workspace.setName(name);
        workspace.setDescription(description);

        return workspaceRepository.save(workspace);
    }

    @Override
    public void deleteWorkspace(Long id) {
        if (!workspaceRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Workspace not found with id: " + id
            );
        }
        workspaceRepository.deleteById(id);
    }


}
