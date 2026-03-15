package com.smj.workhub.workspace.security;

public interface WorkspaceAccessService {

    /**
     * Verifies that a user has access to the given workspace.
     * Throws AccessDeniedException if the user is not a member.
     */
    void verifyWorkspaceAccess(Long workspaceId, Long userId);

}
