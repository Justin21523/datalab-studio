package com.justin.datalab.dataset;

import com.justin.datalab.shared.dto.DatasetColumnDto;
import com.justin.datalab.shared.dto.DatasetDto;

import java.util.List;

/**
 * Maps {@link Dataset} entities to their shared DTO representations so that JPA
 * entities never leak out of the service layer.
 */
public final class DatasetMapper {

    private DatasetMapper() {
    }

    /** Maps a dataset without its columns (for list responses). */
    public static DatasetDto toSummaryDto(Dataset dataset) {
        return toDto(dataset, null);
    }

    /** Maps a dataset including its column schema (for detail responses). */
    public static DatasetDto toDetailDto(Dataset dataset) {
        List<DatasetColumnDto> columns = dataset.getColumns().stream()
                .map(DatasetMapper::toColumnDto)
                .toList();
        return toDto(dataset, columns);
    }

    public static DatasetColumnDto toColumnDto(DatasetColumn column) {
        return new DatasetColumnDto(
                column.getId(),
                column.getName(),
                column.getPosition(),
                column.getColumnType(),
                column.isNullable()
        );
    }

    private static DatasetDto toDto(Dataset dataset, List<DatasetColumnDto> columns) {
        return new DatasetDto(
                dataset.getId(),
                dataset.getName(),
                dataset.getOriginalFileName(),
                dataset.getStatus(),
                dataset.getRowCount(),
                dataset.getColumnCount(),
                dataset.getFileSizeBytes(),
                dataset.getCreatedAt(),
                columns
        );
    }
}
