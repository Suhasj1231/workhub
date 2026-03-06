package com.smj.workhub.workspace.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Workspace response")
public record WorkspaceResponse(

        @Schema(
                description = "Unique identifier of the workspace",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Workspace name",
                example = "Engineering"
        )
        String name,

        @Schema(
                description = "Workspace description",
                example = "Handles engineering projects"
        )
        String description,

        @Schema(
                description = "Workspace creation timestamp (UTC)",
                example = "2026-03-01T10:15:30Z"
        )
        Instant createdAt,

        @Schema(
                description = "Last update timestamp (UTC)",
                example = "2026-03-05T14:20:10Z"
        )
        Instant updatedAt
) {}

