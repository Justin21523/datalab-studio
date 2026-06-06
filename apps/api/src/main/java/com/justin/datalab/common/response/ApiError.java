package com.justin.datalab.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Error payload carried by {@link ApiResponse} when a request fails.
 *
 * @param code    stable machine-readable error code (e.g. {@code NOT_FOUND})
 * @param message human-readable description
 * @param details optional per-field validation errors
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String code,
        String message,
        List<FieldError> details
) {

    public ApiError(String code, String message) {
        this(code, message, null);
    }

    /**
     * A single field-level validation failure.
     *
     * @param field   the offending field name
     * @param message why it failed
     */
    public record FieldError(String field, String message) {
    }
}
