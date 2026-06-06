package com.justin.datalab.common.pagination;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Lightweight, serialization-friendly view of a {@link Page}.
 *
 * @param <T>           element type
 * @param content       page contents
 * @param page          zero-based page index
 * @param size          page size
 * @param totalElements total number of elements across all pages
 * @param totalPages    total number of pages
 * @param last          whether this is the last page
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {

    /**
     * Maps a Spring {@link Page} of entities into a {@code PageResponse} of DTOs.
     */
    public static <E, T> PageResponse<T> of(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
