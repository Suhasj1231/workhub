package com.smj.workhub.workspace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a workspace")
public record CreateWorkspaceRequest(

        @Schema(description = "Unique workspace name", example = "Engineering")
        @NotBlank
        @Size(max = 100)
        String name,

        @Schema(description = "Optional description", example = "Core engineering team")
        @Size(max = 500)
        String description
) {}

