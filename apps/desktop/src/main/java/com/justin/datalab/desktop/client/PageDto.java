package com.justin.datalab.desktop.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Client-side mirror of the backend's paginated response.
 *
 * @param <T> element type
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PageDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}
