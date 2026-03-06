package com.smj.workhub.task.dto;

import com.smj.workhub.task.entity.TaskPriority;
import com.smj.workhub.task.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateTaskRequest(

        @NotBlank(message = "Task title is required")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        TaskStatus status,

        @jakarta.validation.constraints.NotNull(message = "Task priority must be provided")
        TaskPriority priority,

        @jakarta.validation.constraints.Future(message = "Due date must be in the future")
        Instant dueDate

) {}