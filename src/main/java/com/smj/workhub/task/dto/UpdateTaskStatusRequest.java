package com.smj.workhub.task.dto;

import com.smj.workhub.task.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body used to update only the status of a task")
public record UpdateTaskStatusRequest(
        @Schema(
                description = "New status for the task",
                example = "IN_PROGRESS"
        )
        @NotNull(message = "Task status must be provided")
        TaskStatus status
) {}