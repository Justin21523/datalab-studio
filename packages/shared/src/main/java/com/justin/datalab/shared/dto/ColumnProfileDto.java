package com.justin.datalab.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.justin.datalab.shared.enums.ColumnType;

import java.util.List;

/**
 * Profiling summary for a single column.
 *
 * <p>Presence/uniqueness metrics are always populated. Exactly one of
 * {@code numericStats} (numeric columns) or {@code topCategories} (categorical
 * columns) is populated; the other is {@code null} and omitted from JSON.</p>
 *
 * @param column            column name
 * @param type              inferred logical type
 * @param missingCount      number of missing (empty) values
 * @param missingPercentage missing values as a percentage of total rows (0-100)
 * @param uniqueCount       number of distinct non-missing values
 * @param numericStats      descriptive statistics for numeric columns (nullable)
 * @param topCategories     most frequent values for categorical columns (nullable)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ColumnProfileDto(
        String column,
        ColumnType type,
        long missingCount,
        double missingPercentage,
        long uniqueCount,
        NumericStatsDto numericStats,
        List<CategoryCountDto> topCategories
) {
}
