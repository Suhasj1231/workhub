package com.smj.workhub.workspace.service.impl;

import com.smj.workhub.common.exception.AccessDeniedException;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.project.entity.Project;
import com.smj.workhub.security.principal.UserPrincipal;
import com.smj.workhub.task.entity.Task;
import com.smj.workhub.task.repository.TaskRepository;
import com.smj.workhub.workspace.entity.WorkspaceMember;
import com.smj.workhub.project.repository.ProjectRepository;
import com.smj.workhub.workspace.entity.WorkspaceRole;
import com.smj.workhub.workspace.repository.WorkspaceMemberRepository;
import com.smj.workhub.workspace.service.WorkspaceAccessService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceAccessServiceImpl implements WorkspaceAccessService {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public WorkspaceAccessServiceImpl(WorkspaceMemberRepository workspaceMemberRepository,
                                      ProjectRepository projectRepository,
                                      TaskRepository taskRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    private WorkspaceMember getMembership(Long workspaceId) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return workspaceMemberRepository
                .findByWorkspaceIdAndUserIdAndDeletedFalse(workspaceId, principal.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workspace membership not found for user"
                        )
                );
    }

    @Override
    public void verifyWorkspaceAccess(Long workspaceId) {
        // VIEWER and above
        getMembership(workspaceId);
    }

    @Override
    public void verifyWorkspaceMember(Long workspaceId) {

        WorkspaceMember membership = getMembership(workspaceId);

        WorkspaceRole role = membership.getRole();

        if (role == WorkspaceRole.VIEWER) {
            throw new AccessDeniedException(
                    "Insufficient permission. MEMBER access required."
            );
        }
    }

    @Override
    public void verifyWorkspaceAdmin(Long workspaceId) {

        WorkspaceMember membership = getMembership(workspaceId);

        WorkspaceRole role = membership.getRole();

        if (role != WorkspaceRole.ADMIN && role != WorkspaceRole.OWNER) {
            throw new AccessDeniedException(
                    "Admin privileges required for this operation."
            );
        }
    }

    @Override
    public void verifyWorkspaceOwner(Long workspaceId) {

        WorkspaceMember membership = getMembership(workspaceId);

        if (membership.getRole() != WorkspaceRole.OWNER) {
            throw new AccessDeniedException(
                    "Only workspace owner can perform this operation."
            );
        }
    }

    @Override
    public void verifyProjectAccess(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Long workspaceId = project.getWorkspace().getId();

        verifyWorkspaceAccess(workspaceId);
    }

    @Override
    public void verifyTaskAccess(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Long projectId = task.getProject().getId();

        verifyProjectAccess(projectId);
    }
}
