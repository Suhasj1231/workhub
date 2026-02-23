package com.smj.workhub.workspace.service;

import com.smj.workhub.workspace.entity.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkspaceService {

    Workspace createWorkspace(String name, String description);

    Workspace getWorkspaceById(Long id);

    // 🔥 Updated method (filtering + pagination + sorting)
    Page<Workspace> getAllWorkspaces(
            Boolean deleted,
            String name,
            Pageable pageable
    );

    Workspace updateWorkspace(Long id, String name, String description);

    void deleteWorkspace(Long id);
}
