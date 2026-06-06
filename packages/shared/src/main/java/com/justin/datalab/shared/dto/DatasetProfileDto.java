package com.justin.datalab.shared.dto;

import java.util.List;

/**
 * Profiling summary for a whole dataset.
 *
 * @param datasetId   the profiled dataset
 * @param rowCount    total number of data rows
 * @param columnCount number of columns
 * @param columns     per-column profiling results
 */
public record DatasetProfileDto(
        Long datasetId,
        long rowCount,
        int columnCount,
        List<ColumnProfileDto> columns
) {
}
