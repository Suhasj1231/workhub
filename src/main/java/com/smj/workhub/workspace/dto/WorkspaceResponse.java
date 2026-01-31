package com.smj.workhub.workspace.dto;

import java.time.Instant;

public record WorkspaceResponse(
        Long id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {}
