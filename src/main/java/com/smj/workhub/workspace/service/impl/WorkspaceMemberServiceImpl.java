package com.smj.workhub.workspace.service.impl;

import com.smj.workhub.common.exception.DuplicateResourceException;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.workspace.entity.Workspace;
import com.smj.workhub.workspace.entity.WorkspaceMember;
import com.smj.workhub.workspace.entity.WorkspaceRole;
import com.smj.workhub.workspace.repository.WorkspaceMemberRepository;
import com.smj.workhub.workspace.repository.WorkspaceRepository;
import com.smj.workhub.workspace.service.WorkspaceMemberService;
import com.smj.workhub.user.entity.User;
import com.smj.workhub.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceMemberServiceImpl.class);

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceMemberServiceImpl(
            WorkspaceMemberRepository workspaceMemberRepository,
            WorkspaceRepository workspaceRepository,
            UserRepository userRepository
    ) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    // -------- Add member --------
    @Override
    public WorkspaceMember addMember(Long workspaceId, Long userId) {

        log.info("Adding user {} to workspace {}", userId, workspaceId);

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found with id: " + workspaceId)
                );

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId)
                );

        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)) {
            throw new DuplicateResourceException(
                    "User is already a member of this workspace"
            );
        }

        WorkspaceMember membership = new WorkspaceMember();
        membership.setWorkspace(workspace);
        membership.setUser(user);
        membership.setRole(WorkspaceRole.MEMBER);

        WorkspaceMember saved = workspaceMemberRepository.save(membership);

        log.info("User {} added to workspace {}", userId, workspaceId);

        return saved;
    }

    // -------- Remove member --------
    @Override
    public void removeMember(Long workspaceId, Long userId) {

        log.warn("Removing user {} from workspace {}", userId, workspaceId);

        WorkspaceMember membership = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Membership not found")
                );

        workspaceMemberRepository.delete(membership);

        log.info("User {} removed from workspace {}", userId, workspaceId);
    }

    // -------- List members --------
    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceMember> getWorkspaceMembers(Long workspaceId) {

        log.debug("Fetching members for workspace {}", workspaceId);

        if (!workspaceRepository.existsById(workspaceId)) {
            throw new ResourceNotFoundException("Workspace not found with id: " + workspaceId);
        }

        return workspaceMemberRepository.findByWorkspaceId(workspaceId);
    }

    // -------- Membership check --------
    @Override
    @Transactional(readOnly = true)
    public boolean isMember(Long workspaceId, Long userId) {

        return workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
    }
}
