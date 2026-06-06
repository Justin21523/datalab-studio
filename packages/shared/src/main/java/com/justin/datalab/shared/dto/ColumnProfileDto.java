package com.justin.datalab.shared.dto;

import com.justin.datalab.shared.enums.ColumnType;

/**
 * Profiling summary for a single column.
 *
 * <p>Phase 1 reports presence/uniqueness metrics only. Numeric statistics
 * (min/max/mean/median/stddev) and category frequencies are added in a later
 * phase.</p>
 *
 * @param column            column name
 * @param type              inferred logical type
 * @param missingCount      number of missing (empty) values
 * @param missingPercentage missing values as a percentage of total rows (0-100)
 * @param uniqueCount       number of distinct non-missing values
 */
public record ColumnProfileDto(
        String column,
        ColumnType type,
        long missingCount,
        double missingPercentage,
        long uniqueCount
) {
}
