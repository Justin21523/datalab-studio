package com.justin.datalab.shared.constant;

/**
 * Canonical API paths shared by the backend (route mapping) and the desktop
 * client (request building). Keeping them in one place prevents the two sides
 * from drifting apart.
 */
public final class ApiPaths {

    public static final String API_BASE = "/api/v1";

    public static final String HEALTH = API_BASE + "/health";

    public static final String DATASETS = API_BASE + "/datasets";
    public static final String DATASETS_IMPORT_CSV = DATASETS + "/import/csv";

    /** Relative sub-paths under {@link #DATASETS}/{id}. */
    public static final String DATASET_PREVIEW = "/preview";
    public static final String DATASET_PROFILE = "/profile";

    private ApiPaths() {
        // Constants holder; not instantiable.
    }
}
