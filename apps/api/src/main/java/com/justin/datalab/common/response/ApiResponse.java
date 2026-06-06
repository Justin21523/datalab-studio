package com.justin.datalab.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Uniform response envelope returned by every endpoint.
 *
 * <p>On success, {@code data} is populated and {@code error} is {@code null}.
 * On failure, {@code error} is populated and {@code data} is {@code null}.</p>
 *
 * @param <T>       type of the success payload
 * @param success   whether the request succeeded
 * @param data      success payload (null on failure)
 * @param error     error payload (null on success)
 * @param timestamp server time the response was produced
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        ApiError error,
        Instant timestamp
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> fail(ApiError error) {
        return new ApiResponse<>(false, null, error, Instant.now());
    }
}
