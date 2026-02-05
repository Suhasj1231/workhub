package com.smj.workhub.common.error;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Standard error response")
public class ApiError {

    @Schema(
            description = "HTTP status code",
            example = "404"
    )
    private int status;

    @Schema(
            description = "Human-readable error message",
            example = "Workspace not found with id: 42"
    )
    private String message;

    @Schema(
            description = "Request path where the error occurred",
            example = "/api/workspaces/42"
    )
    private String path;

    @Schema(
            description = "Time when the error occurred (UTC)",
            example = "2026-01-29T17:30:00Z"
    )
    private Instant timestamp;

    // constructors + getters
    public ApiError(int status, String message, String path, Instant timestamp) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
