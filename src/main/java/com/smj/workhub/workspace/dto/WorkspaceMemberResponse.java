package com.smj.workhub.workspace.dto;

import com.smj.workhub.workspace.entity.WorkspaceRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Workspace member response")
public record WorkspaceMemberResponse(

        @Schema(description = "Membership ID", example = "1")
        Long id,

        @Schema(description = "Workspace ID", example = "10")
        Long workspaceId,

        @Schema(description = "User ID", example = "5")
        Long userId,

        @Schema(description = "Role of the user in the workspace")
        WorkspaceRole role,

        @Schema(description = "Membership creation timestamp")
        Instant createdAt
) {}
