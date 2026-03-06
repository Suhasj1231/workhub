//package com.smj.workhub.project.dto;
//
//import java.time.Instant;
//
//public record ProjectResponse(
//        Long id,
//        Long workspaceId,
//        String name,
//        String description,
//        Instant createdAt,
//        Instant updatedAt
//) {}


package com.smj.workhub.project.dto;

import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProjectResponse(

        @Schema(
                description = "Unique identifier of the project",
                example = "10"
        )
        Long id,

        @Schema(
                description = "Workspace to which this project belongs",
                example = "1"
        )
        Long workspaceId,

        @Schema(
                description = "Project name (unique within a workspace)",
                example = "Payment Service"
        )
        String name,

        @Schema(
                description = "Optional project description",
                example = "Handles payment related services"
        )
        String description,

        @Schema(
                description = "Indicates whether the project is soft-deleted",
                example = "false"
        )
        boolean deleted,

        @Schema(
                description = "Timestamp when the project was created (UTC)",
                example = "2026-03-01T10:15:30Z"
        )
        Instant createdAt,

        @Schema(
                description = "Timestamp when the project was last updated (UTC)",
                example = "2026-03-05T14:20:10Z"
        )
        Instant updatedAt
) {}