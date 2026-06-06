package com.justin.datalab.common.exception;

/**
 * Thrown for client errors that are not bean-validation failures
 * (e.g. an unreadable upload or an unsupported file). Mapped to HTTP 400.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
