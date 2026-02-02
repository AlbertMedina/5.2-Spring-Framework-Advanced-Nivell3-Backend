package com.videostore.videostore.web.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
}
