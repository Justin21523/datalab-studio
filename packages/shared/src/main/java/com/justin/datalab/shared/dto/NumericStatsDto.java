package com.justin.datalab.shared.dto;

/**
 * Descriptive statistics for a numeric column.
 *
 * @param min    smallest value
 * @param max    largest value
 * @param mean   arithmetic mean
 * @param median 50th percentile
 * @param stddev standard deviation
 */
public record NumericStatsDto(
        double min,
        double max,
        double mean,
        double median,
        double stddev
) {
}
