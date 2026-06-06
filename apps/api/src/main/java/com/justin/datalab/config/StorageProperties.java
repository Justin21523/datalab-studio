package com.justin.datalab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for local dataset file storage and preview behaviour.
 *
 * <p>Bound from the {@code datalab.storage} prefix in {@code application.yml}.</p>
 *
 * @param dataDir     root directory for stored raw dataset files
 * @param previewRows default number of rows returned by the preview endpoint
 */
@ConfigurationProperties(prefix = "datalab.storage")
public record StorageProperties(
        String dataDir,
        int previewRows
) {

    public StorageProperties {
        if (dataDir == null || dataDir.isBlank()) {
            dataDir = "./data";
        }
        if (previewRows <= 0) {
            previewRows = 50;
        }
    }
}
