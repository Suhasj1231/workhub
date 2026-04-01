package com.smj.workhub.activity.dto;

import com.smj.workhub.activity.entity.ActivityAction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Activity response")
public record ActivityResponse(

        Long id,

        Long userId,

        ActivityAction action,

        Long workspaceId,

        Long projectId,

        Long taskId,

        String description,

        String metadata,

        Instant createdAt
) {}