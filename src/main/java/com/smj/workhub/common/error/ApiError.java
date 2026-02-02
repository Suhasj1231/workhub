package com.smj.workhub.common.error;

import java.time.Instant;

public record ApiError(
        int status,
        String message,
        String path,
        Instant timestamp
) {}
