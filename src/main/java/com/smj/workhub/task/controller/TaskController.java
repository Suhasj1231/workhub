package com.smj.workhub.task.controller;

import com.smj.workhub.common.error.ApiError;
import com.smj.workhub.task.dto.*;
import com.smj.workhub.task.entity.Task;
import com.smj.workhub.task.entity.TaskPriority;
import com.smj.workhub.task.entity.TaskStatus;
import com.smj.workhub.task.service.TaskService;
import com.smj.workhub.workspace.service.WorkspaceAccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TaskController {

    private final TaskService taskService;
    private final WorkspaceAccessService workspaceAccessService;

    public TaskController(
            TaskService taskService,
            WorkspaceAccessService workspaceAccessService
    ) {
        this.taskService = taskService;
        this.workspaceAccessService = workspaceAccessService;
    }

    // CREATE TASK
    @Operation(
            summary = "Create task under project",
            description = "Creates a new task inside a project"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        Task taskPreview = taskService.getProjectPreview(projectId);
        workspaceAccessService.verifyWorkspaceMember(taskPreview.getProject().getWorkspace().getId());
        Task task = taskService.createTask(projectId, request);
        return toResponse(task);
    }

    // LIST TASKS WITH FILTERS
    @Operation(
            summary = "List tasks under project",
            description = "Returns paginated tasks with optional filters"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of tasks",
            content = @Content(
                    array = @ArraySchema(
                            schema = @Schema(implementation = TaskResponse.class)
                    )
            )
    )
    @GetMapping("/projects/{projectId}/tasks")
    public Page<TaskResponse> getTasks(
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean includeDeleted,
            @RequestParam(required = false) Boolean assignedToMe,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Task taskPreview = taskService.getProjectPreview(projectId);
        workspaceAccessService.verifyWorkspaceAccess(taskPreview.getProject().getWorkspace().getId());

        return taskService.getTasks(
                        projectId,
                        status,
                        priority,
                        search,
                        includeDeleted,
                        assignedToMe,
                        pageable
                );
    }

    // GET TASK BY ID
    @Operation(
            summary = "Get task by id",
            description = "Returns a specific task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping("/tasks/{taskId}")
    public TaskResponse getTaskById(@PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        workspaceAccessService.verifyWorkspaceAccess(task.getProject().getWorkspace().getId());
        return toResponse(task);
    }

    // UPDATE TASK
    @Operation(
            summary = "Update task",
            description = "Updates task details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PutMapping("/tasks/{taskId}")
    public TaskResponse updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        Task existing = taskService.getTaskById(taskId);
        workspaceAccessService.verifyWorkspaceMember(existing.getProject().getWorkspace().getId());

        Task task = taskService.updateTask(taskId, request);
        return toResponse(task);
    }

    // DELETE TASK
    @Operation(
            summary = "Delete task",
            description = "Soft deletes a task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @DeleteMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long taskId) {
        Task existing = taskService.getTaskById(taskId);
        workspaceAccessService.verifyWorkspaceMember(existing.getProject().getWorkspace().getId());
        taskService.deleteTask(taskId);
    }

    // RESTORE TASK
    @Operation(
            summary = "Restore task",
            description = "Restores a soft deleted task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task restored"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PatchMapping("/tasks/{taskId}/restore")
    public TaskResponse restoreTask(@PathVariable Long taskId) {
        Task existing = taskService.getTaskByIdIncludingDeleted(taskId);
        workspaceAccessService.verifyWorkspaceMember(existing.getProject().getWorkspace().getId());
        Task task = taskService.restoreTask(taskId);
        return toResponse(task);
    }


    @Operation(
            summary = "Update task status",
            description = "Updates only the status of an existing task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status value",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PatchMapping("/tasks/{taskId}/status")
    public TaskResponse updateStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request
    ) {
        Task existing = taskService.getTaskById(taskId);
        workspaceAccessService.verifyWorkspaceMember(existing.getProject().getWorkspace().getId());

        Task task = taskService.updateTaskStatus(taskId, request.status());
        return toResponse(task);
    }

    @Operation(
            summary = "Assign task",
            description = "Assigns a task to a user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task assigned successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task or User not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PatchMapping("/tasks/{taskId}/assign")
    public TaskResponse assignTask(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest request
    ) {
        Task existing = taskService.getTaskById(taskId);
        workspaceAccessService.verifyWorkspaceMember(existing.getProject().getWorkspace().getId());

        Task task = taskService.assignTask(taskId, request.getUserId());
        return toResponse(task);
    }

    // ENTITY → DTO MAPPER
    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getAssignedTo(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

}
