package com.smj.workhub.workspace.service;

import com.smj.workhub.workspace.entity.Workspace;

import java.util.List;

public interface WorkspaceService {

    Workspace createWorkspace(String name, String description);

    Workspace getWorkspaceById(Long id);

    List<Workspace> getAllWorkspaces();

    Workspace updateWorkspace(Long id, String name, String description);

    void deleteWorkspace(Long id);
}
