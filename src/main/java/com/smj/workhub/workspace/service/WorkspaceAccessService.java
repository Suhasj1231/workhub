package com.smj.workhub.workspace.service;

/**
 * Centralized authorization checks for workspace access.
 * This service ensures users have proper roles before performing actions.
 */
public interface WorkspaceAccessService {

    /**
     * Verify the user has at least VIEWER access to the workspace.
     */
    void verifyWorkspaceAccess(Long workspaceId);

    /**
     * Verify the user has MEMBER level access or higher.
     */
    void verifyWorkspaceMember(Long workspaceId);

    /**
     * Verify the user has ADMIN level access or higher.
     */
    void verifyWorkspaceAdmin(Long workspaceId);

    /**
     * Verify the user is the OWNER of the workspace.
     */
    void verifyWorkspaceOwner(Long workspaceId);

    void verifyProjectAccess(Long projectId);

    void verifyTaskAccess(Long taskId);
}
