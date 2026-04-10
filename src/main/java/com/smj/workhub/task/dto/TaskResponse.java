package com.smj.workhub.task.dto;

import com.smj.workhub.task.entity.TaskPriority;
import com.smj.workhub.task.entity.TaskStatus;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task response returned by the API")
public record TaskResponse(

        @Schema(description = "Unique identifier of the task", example = "1")
        Long id,

        @Schema(description = "Project ID to which this task belongs", example = "10")
        Long projectId,

        @Schema(description = "User ID to whom this task is assigned", example = "5")
        Long assignedTo,

        @Schema(description = "Task title", example = "Implement authentication")
        String title,

        @Schema(description = "Detailed description of the task", example = "Add JWT based authentication")
        String description,

        @Schema(description = "Current status of the task", example = "IN_PROGRESS")
        TaskStatus status,

        @Schema(description = "Priority level of the task", example = "HIGH")
        TaskPriority priority,

        @Schema(description = "Optional due date of the task", example = "2026-03-10T10:15:30Z")
        Instant dueDate,

        @Schema(description = "Timestamp when the task was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the task was last updated")
        Instant updatedAt

) {}
