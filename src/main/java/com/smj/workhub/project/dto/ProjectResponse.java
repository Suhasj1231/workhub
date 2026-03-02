package com.smj.workhub.project.dto;

import java.time.Instant;

public record ProjectResponse(
        Long id,
        Long workspaceId,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {}