package com.smj.workhub.workspace.controller;

import com.smj.workhub.common.error.ApiError;
import com.smj.workhub.workspace.dto.CreateWorkspaceRequest;
import com.smj.workhub.workspace.dto.UpdateWorkspaceRequest;
import com.smj.workhub.workspace.dto.WorkspaceResponse;
import com.smj.workhub.workspace.entity.Workspace;
import com.smj.workhub.workspace.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Workspace", description = "Workspace management APIs")
@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    // -------- CREATE --------
    @Operation(
            summary = "Create a new workspace",
            description = "Creates a workspace with a unique name"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Workspace created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Workspace name already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceResponse create(@Valid @RequestBody CreateWorkspaceRequest request) {

        Workspace workspace = workspaceService.createWorkspace(
                request.name(),
                request.description()
        );

        return toResponse(workspace);
    }

    // -------- GET BY ID --------
    @Operation(
            summary = "Get workspace by ID",
            description = "Returns workspace details by ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workspace found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Workspace not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping("/{id}")
    public WorkspaceResponse getById(@PathVariable Long id) {
        return toResponse(workspaceService.getWorkspaceById(id));
    }

    // -------- GET ALL --------
    @Operation(
            summary = "Get paginated list of workspaces",
            description = "Returns paginated workspaces with optional filtering by name and deleted flag"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of workspaces",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = WorkspaceResponse.class)
                            )
                    )
            )
    })
    @GetMapping
    public Page<WorkspaceResponse> getAll(

            @Parameter(description = "Filter by deleted flag (true/false)")
            @RequestParam(required = false) Boolean deleted,

            @Parameter(description = "Filter by workspace name (contains, case-insensitive)")
            @RequestParam(required = false) String name,

            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return workspaceService
                .getAllWorkspaces(deleted, name, pageable)
                .map(this::toResponse);
    }


    // -------- UPDATE --------
    @Operation(
            summary = "Update workspace",
            description = "Updates workspace name and description"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workspace updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Workspace not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Workspace name already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public WorkspaceResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkspaceRequest request
    ) {
        Workspace updated = workspaceService.updateWorkspace(
                id,
                request.name(),
                request.description()
        );
        return toResponse(updated);
    }

    // -------- DELETE --------
    @Operation(
            summary = "Delete workspace",
            description = "Deletes a workspace by ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Workspace deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Workspace not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        workspaceService.deleteWorkspace(id);
    }

    // -------- MAPPER --------
    private WorkspaceResponse toResponse(Workspace workspace) {
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getDescription(),
                workspace.getCreatedAt(),
                workspace.getUpdatedAt()
        );
    }

    @Operation(
            summary = "Restore deleted workspace",
            description = "Restores a soft-deleted workspace"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Workspace restored successfully",
                    content = @Content(
                            schema = @Schema(implementation = WorkspaceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Workspace not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Workspace already active",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate workspace name conflict",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PatchMapping("/{id}/restore")
    public WorkspaceResponse restoreWorkspace(@PathVariable Long id) {
        return toResponse(workspaceService.restoreWorkspace(id));
    }
}
