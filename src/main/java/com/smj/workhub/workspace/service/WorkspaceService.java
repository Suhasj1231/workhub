package com.smj.workhub.workspace.service;

import com.smj.workhub.workspace.entity.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkspaceService {

    Workspace createWorkspace(String name, String description);

    Workspace getWorkspaceById(Long id);


    Page<Workspace> getAllWorkspaces(Pageable pageable);

    Workspace updateWorkspace(Long id, String name, String description);

    void deleteWorkspace(Long id);
}
