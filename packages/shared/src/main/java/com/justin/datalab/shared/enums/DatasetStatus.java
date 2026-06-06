package com.justin.datalab.shared.enums;

/**
 * Lifecycle status of an imported dataset.
 */
public enum DatasetStatus {

    /** Import is in progress; the dataset is not yet usable. */
    IMPORTING,

    /** Import completed successfully; the dataset can be previewed and profiled. */
    READY,

    /** Import failed; see logs for the cause. */
    FAILED
}
