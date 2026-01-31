package com.smj.workhub.workspace.controller;

import com.smj.workhub.workspace.dto.CreateWorkspaceRequest;
import com.smj.workhub.workspace.dto.UpdateWorkspaceRequest;
import com.smj.workhub.workspace.dto.WorkspaceResponse;
import com.smj.workhub.workspace.entity.Workspace;
import com.smj.workhub.workspace.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    // -------- CREATE --------
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
    @GetMapping("/{id}")
    public WorkspaceResponse getById(@PathVariable Long id) {
        return toResponse(workspaceService.getWorkspaceById(id));
    }

    // -------- GET ALL --------
    @GetMapping
    public List<WorkspaceResponse> getAll() {
        return workspaceService.getAllWorkspaces()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // -------- UPDATE --------
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
}
