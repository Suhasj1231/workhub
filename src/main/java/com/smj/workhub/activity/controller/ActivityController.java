package com.smj.workhub.activity.controller;

import com.smj.workhub.activity.entity.ActivityAction;
import com.smj.workhub.activity.entity.ActivityLog;
import com.smj.workhub.activity.dto.ActivityResponse;
import com.smj.workhub.activity.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
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
}