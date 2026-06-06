package com.justin.datalab.shared.dto;

import java.util.List;

/**
 * A preview of the first rows of a dataset.
 *
 * @param datasetId    the dataset this preview belongs to
 * @param columns      column header names, in order
 * @param rows         row values as strings, each row aligned to {@code columns}
 * @param returnedRows number of rows actually returned in this preview
 * @param totalRows    total number of data rows in the dataset
 */
public record DatasetPreviewDto(
        Long datasetId,
        List<String> columns,
        List<List<String>> rows,
        int returnedRows,
        long totalRows
) {
}
