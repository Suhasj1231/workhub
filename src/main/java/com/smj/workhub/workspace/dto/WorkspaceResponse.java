package com.smj.workhub.workspace.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Workspace response")
public record WorkspaceResponse(

        Long id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {}

