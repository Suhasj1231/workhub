package com.smj.workhub.project.controller;

import com.smj.workhub.project.dto.CreateProjectRequest;
import com.smj.workhub.project.dto.ProjectResponse;
import com.smj.workhub.project.dto.UpdateProjectRequest;
import com.smj.workhub.project.entity.Project;
import com.smj.workhub.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import com.smj.workhub.common.error.ApiError;

import com.smj.workhub.workspace.service.WorkspaceAccessService;

@RestController
@RequestMapping("/api/v1")
public class ProjectController {

    private final ProjectService projectService;
    private final WorkspaceAccessService workspaceAccessService;

    public ProjectController(
            ProjectService projectService,
            WorkspaceAccessService workspaceAccessService
    ) {
        this.projectService = projectService;
        this.workspaceAccessService = workspaceAccessService;
    }

    // -------- CREATE --------

    @Operation(
            summary = "Create project",
            description = "Creates a new project under a workspace"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Workspace not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate project name",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/workspaces/{workspaceId}/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(
            @PathVariable Long workspaceId,
            @Valid @RequestBody CreateProjectRequest request
    ) {
        workspaceAccessService.verifyWorkspaceMember(workspaceId);

        Project project = projectService.createProject(
                workspaceId,
                request.name(),
                request.description()
        );

        return toResponse(project);
    }

    // -------- LIST --------

    @Operation(
            summary = "List projects of workspace",
            description = "Returns paginated list of projects with optional search and includeDeleted filter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of projects",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ProjectResponse.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Workspace not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping("/workspaces/{workspaceId}/projects")
    public Page<ProjectResponse> list(
            @PathVariable Long workspaceId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean includeDeleted,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        workspaceAccessService.verifyWorkspaceAccess(workspaceId);

        return projectService
                .getProjects(workspaceId, search, includeDeleted, pageable)
                .map(this::toResponse);
    }

    // -------- GET BY ID --------

    @Operation(
            summary = "Get project by ID",
            description = "Returns project details by project ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project found",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping("/projects/{projectId}")
    public ProjectResponse getById(@PathVariable Long projectId) {
        Project project = projectService.getProjectById(projectId);
        workspaceAccessService.verifyWorkspaceAccess(project.getWorkspace().getId());
        return toResponse(project);
    }

    // -------- UPDATE --------

    @Operation(
            summary = "Update project",
            description = "Updates project name and description"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate project name",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PutMapping("/projects/{projectId}")
    public ProjectResponse update(
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequest request
    ) {
        Project existing = projectService.getProjectById(projectId);
        workspaceAccessService.verifyWorkspaceAdmin(existing.getWorkspace().getId());

        Project project = projectService.updateProject(
                projectId,
                request.name(),
                request.description()
        );

        return toResponse(project);
    }

    // -------- SOFT DELETE --------

    @Operation(
            summary = "Soft delete project",
            description = "Marks the project as deleted"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Project deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @DeleteMapping("/projects/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long projectId) {
        Project existing = projectService.getProjectById(projectId);
        workspaceAccessService.verifyWorkspaceAdmin(existing.getWorkspace().getId());
        projectService.deleteProject(projectId);
    }

    // -------- RESTORE --------

    @Operation(
            summary = "Restore project",
            description = "Restores a previously soft-deleted project"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project restored",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Deleted project not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PatchMapping("/projects/{projectId}/restore")
    public ProjectResponse restore(@PathVariable Long projectId) {
        Project project = projectService.restoreProject(projectId);
        workspaceAccessService.verifyWorkspaceAdmin(project.getWorkspace().getId());
        return toResponse(project);
    }

    // -------- MAPPER --------

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getWorkspace().getId(),
                project.getName(),
                project.getDescription(),
                project.isDeleted(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

}
