package com.smj.workhub.workspace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update an existing workspace")
public record UpdateWorkspaceRequest(

        @Schema(
                description = "Updated workspace name (must be unique)",
                example = "Engineering Team"
        )
        @NotBlank(message = "Workspace name must not be blank")
        @Size(min = 3, max = 100, message = "Workspace name must be between 3 and 100 characters")
        String name,

        @Schema(
                description = "Updated workspace description",
                example = "Handles all engineering initiatives"
        )
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description
) {}
