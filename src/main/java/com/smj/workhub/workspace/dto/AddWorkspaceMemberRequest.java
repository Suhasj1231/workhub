package com.smj.workhub.workspace.dto;

import com.smj.workhub.workspace.entity.WorkspaceRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to add a user to a workspace")
public record AddWorkspaceMemberRequest(

        @NotNull
        @Schema(description = "User ID to add", example = "5")
        Long userId,

        @Schema(description = "Role of the user in the workspace", example = "MEMBER")
        WorkspaceRole role
) {}