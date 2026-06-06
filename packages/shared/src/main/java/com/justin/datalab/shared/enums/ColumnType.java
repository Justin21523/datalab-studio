package com.justin.datalab.shared.enums;

/**
 * Inferred logical type of a dataset column.
 *
 * <p>Phase 1 uses a small, conservative set of types derived from sampling the
 * imported file. Anything that does not clearly match a more specific type
 * falls back to {@link #STRING}.</p>
 */
public enum ColumnType {

    STRING,
    INTEGER,
    DECIMAL,
    BOOLEAN,
    DATE,
    DATETIME,

    /** Used when a column is empty or its type cannot be determined. */
    UNKNOWN
}
