package com.smj.workhub.workspace.controller;

import com.smj.workhub.workspace.dto.AddWorkspaceMemberRequest;
import com.smj.workhub.workspace.dto.WorkspaceMemberResponse;
import com.smj.workhub.workspace.entity.WorkspaceMember;
import com.smj.workhub.workspace.service.WorkspaceMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
@Tag(name = "Workspace Members", description = "APIs for managing workspace membership")
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;

    public WorkspaceMemberController(WorkspaceMemberService workspaceMemberService) {
        this.workspaceMemberService = workspaceMemberService;
    }

    // -------- Add member to workspace --------
    @Operation(summary = "Add user to workspace")
    @PostMapping("/{workspaceId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceMemberResponse addMember(
            @PathVariable Long workspaceId,
            @Valid @RequestBody AddWorkspaceMemberRequest request
    ) {

        WorkspaceMember member = workspaceMemberService.addMember(
                workspaceId,
                request.userId()
        );

        return toResponse(member);
    }

    // -------- List workspace members --------
    @Operation(summary = "Get all members of a workspace")
    @GetMapping("/{workspaceId}/members")
    public List<WorkspaceMemberResponse> getMembers(
            @PathVariable Long workspaceId
    ) {
        return workspaceMemberService.getWorkspaceMembers(workspaceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // -------- Remove member from workspace --------
    @Operation(summary = "Remove user from workspace")
    @DeleteMapping("/{workspaceId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @PathVariable Long workspaceId,
            @PathVariable Long userId
    ) {
        workspaceMemberService.removeMember(workspaceId, userId);
    }

    // -------- Mapper --------
    private WorkspaceMemberResponse toResponse(WorkspaceMember member) {
        return new WorkspaceMemberResponse(
                member.getId(),
                member.getWorkspace().getId(),
                member.getUser().getId(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}