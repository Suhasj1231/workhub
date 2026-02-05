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
        @NotBlank
        @Size(max = 100)
        String name,

        @Schema(
                description = "Updated workspace description",
                example = "Handles all engineering initiatives"
        )
        @Size(max = 500)
        String description
) {}
