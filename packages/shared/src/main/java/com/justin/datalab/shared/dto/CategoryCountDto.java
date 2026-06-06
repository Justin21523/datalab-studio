package com.justin.datalab.shared.dto;

/**
 * Frequency of a single value within a categorical column.
 *
 * @param value the (non-missing) value
 * @param count how many times it occurs
 */
public record CategoryCountDto(String value, long count) {
}
