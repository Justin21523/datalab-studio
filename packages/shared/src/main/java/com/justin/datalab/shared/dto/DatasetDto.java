package com.justin.datalab.shared.dto;

import com.justin.datalab.shared.enums.DatasetStatus;

import java.time.Instant;
import java.util.List;

/**
 * Dataset metadata returned by the API.
 *
 * <p>{@code columns} is populated for detail responses and may be {@code null}
 * for list responses to keep payloads small.</p>
 *
 * @param id               persistent identifier
 * @param name             user-facing dataset name
 * @param originalFileName original uploaded file name
 * @param status           lifecycle status
 * @param rowCount         number of data rows (excluding header)
 * @param columnCount      number of columns
 * @param fileSizeBytes    stored file size in bytes
 * @param createdAt        creation timestamp
 * @param columns          inferred schema (nullable for list responses)
 */
public record DatasetDto(
        Long id,
        String name,
        String originalFileName,
        DatasetStatus status,
        long rowCount,
        int columnCount,
        long fileSizeBytes,
        Instant createdAt,
        List<DatasetColumnDto> columns
) {
}
