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

public record ProjectResponse(
        Long id,
        Long workspaceId,
        String name,
        String description,
        boolean deleted,
        Instant createdAt,
        Instant updatedAt
) {}