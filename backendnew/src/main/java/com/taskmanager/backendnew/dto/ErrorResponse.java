package com.taskmanager.backendnew.dto;

import java.time.LocalDateTime;

// A record is a short way to write a class with fields, constructor and getters.
// Spring converts it to JSON automatically.
public record ErrorResponse(
        int status, // HTTP code, e.g. 401
        String error, // short label, e.g. "Unauthorized"
        String message, // readable text for the user
        LocalDateTime timestamp // when it happened
) {
    // Helper so callers don't have to pass the timestamp every time
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, LocalDateTime.now());
    }
}
