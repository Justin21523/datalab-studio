package com.justin.datalab.shared.dto;

import com.justin.datalab.shared.enums.ColumnType;

/**
 * A single column in a dataset's inferred schema.
 *
 * @param id       persistent identifier
 * @param name     column header name
 * @param position zero-based column index within the dataset
 * @param type     inferred logical type
 * @param nullable whether at least one sampled value was missing
 */
public record DatasetColumnDto(
        Long id,
        String name,
        int position,
        ColumnType type,
        boolean nullable
) {
}
