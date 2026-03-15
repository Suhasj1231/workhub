

package com.smj.workhub.workspace.service;

import com.smj.workhub.workspace.entity.WorkspaceMember;

import java.util.List;

public interface WorkspaceMemberService {

    // Add a user to a workspace
    WorkspaceMember addMember(Long workspaceId, Long userId);

    // Remove a user from a workspace
    void removeMember(Long workspaceId, Long userId);

    // List all members of a workspace
    List<WorkspaceMember> getWorkspaceMembers(Long workspaceId);

    // Check whether a user belongs to a workspace
    boolean isMember(Long workspaceId, Long userId);
}