package com.smj.workhub.activity.controller;

import com.smj.workhub.activity.entity.ActivityAction;
import com.smj.workhub.activity.entity.ActivityLog;
import com.smj.workhub.activity.dto.ActivityResponse;
import com.smj.workhub.activity.service.ActivityService;
import com.smj.workhub.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

import com.smj.workhub.workspace.service.WorkspaceAccessService;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final WorkspaceAccessService workspaceAccessService;

    public ActivityController(ActivityService activityService,
                              WorkspaceAccessService workspaceAccessService) {
        this.activityService = activityService;
        this.workspaceAccessService = workspaceAccessService;
    }

    @Operation(
            summary = "Get activities with filtering",
            description = "Fetch activities using filters like workspaceId, projectId, taskId, userId, action, and date range"
    )
    @GetMapping
    public Page<ActivityResponse> getActivities(

            @Parameter(description = "Workspace ID")
            @RequestParam(required = false) Long workspaceId,

            @Parameter(description = "Project ID")
            @RequestParam(required = false) Long projectId,

            @Parameter(description = "Task ID")
            @RequestParam(required = false) Long taskId,

            @Parameter(description = "User ID")
            @RequestParam(required = false) Long userId,

            @Parameter(description = "Activity action")
            @RequestParam(required = false) ActivityAction action,

            @Parameter(description = "From date (ISO format)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,

            @Parameter(description = "To date (ISO format)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to,

            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC
            ) Pageable pageable
    ) {

        return activityService.getActivities(
                workspaceId,
                projectId,
                taskId,
                userId,
                action,
                from,
                to,
                pageable
        ).map(this::toResponse);
    }


    @Operation(
            summary = "Get workspace activity feed",
            description = "Returns activity timeline for a workspace (secure, member-only access)"
    )
    @GetMapping("/workspaces/{workspaceId}")
    public Page<ActivityResponse> getWorkspaceActivities(
            @PathVariable Long workspaceId,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC
            ) Pageable pageable
    ) {
        // 🔐 Security check
        workspaceAccessService.verifyWorkspaceAccess(workspaceId);

        return activityService.getWorkspaceActivities(workspaceId, pageable)
                .map(this::toResponse);
    }

    @Operation(
            summary = "Get project activity feed",
            description = "Returns activity timeline for a project (secure, member-only access via workspace)"
    )
    @GetMapping("/projects/{projectId}")
    public Page<ActivityResponse> getProjectActivities(
            @PathVariable Long projectId,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC
            ) Pageable pageable
    ) {
        // 🔐 Security check (via workspace access indirectly)
        workspaceAccessService.verifyProjectAccess(projectId);

        return activityService.getProjectActivities(projectId, pageable)
                .map(this::toResponse);
    }

    @Operation(
            summary = "Get task activity feed",
            description = "Returns activity timeline for a task (secure, member-only access via project/workspace)"
    )
    @GetMapping("/tasks/{taskId}")
    public Page<ActivityResponse> getTaskActivities(
            @PathVariable Long taskId,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC
            ) Pageable pageable
    ) {
        // 🔐 Security check (via project/workspace access)
        workspaceAccessService.verifyTaskAccess(taskId);

        return activityService.getTaskActivities(taskId, pageable)
                .map(this::toResponse);
    }


    private ActivityResponse toResponse(ActivityLog activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getUserId(),
                activity.getAction(),
                activity.getWorkspaceId(),
                activity.getProjectId(),
                activity.getTaskId(),
                activity.getDescription(),
                activity.getMetadata(),
                activity.getCreatedAt()
        );
    }

    private Long getCurrentUserId() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }

        throw new org.springframework.security.access.AccessDeniedException("Invalid authentication principal");
    }
}