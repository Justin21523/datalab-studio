package com.justin.datalab.profiling;

import com.justin.datalab.dataset.Dataset;
import com.justin.datalab.dataset.DatasetColumn;
import com.justin.datalab.shared.dto.CategoryCountDto;
import com.justin.datalab.shared.dto.ColumnProfileDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;
import com.justin.datalab.shared.dto.NumericStatsDto;
import com.justin.datalab.storage.FileStorageService;
import org.springframework.stereotype.Service;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Computes a profiling summary for a dataset using Tablesaw.
 *
 * <p>For every column it reports missing/unique counts. Numeric columns also get
 * descriptive statistics (min/max/mean/median/stddev); categorical columns get
 * their most frequent values. The dataset's duplicate-row count is reported too.</p>
 */
@Service
public class ProfilingService {

    /** Number of most-frequent values reported per categorical column. */
    private static final int TOP_CATEGORIES = 10;

    private final FileStorageService storage;

    public ProfilingService(FileStorageService storage) {
        this.storage = storage;
    }

    /**
     * Profiles a dataset by loading its stored file into a Tablesaw table. The
     * dataset's columns must already be loaded.
     */
    public DatasetProfileDto profile(Dataset dataset) {
        File file = storage.resolve(dataset.getStoredPath()).toFile();
        Table table = Table.read().csv(CsvReadOptions.builder(file).header(true).build());

        long rowCount = table.rowCount();
        long duplicateRowCount = rowCount - table.copy().dropDuplicateRows().rowCount();

        List<DatasetColumn> columns = dataset.getColumns();
        List<ColumnProfileDto> columnProfiles = new ArrayList<>(columns.size());
        for (int i = 0; i < columns.size(); i++) {
            DatasetColumn metadata = columns.get(i);
            if (i >= table.columnCount()) {
                columnProfiles.add(emptyProfile(metadata));
                continue;
            }
            columnProfiles.add(profileColumn(metadata, table.column(i), rowCount));
        }

        return new DatasetProfileDto(
                dataset.getId(), rowCount, columns.size(), duplicateRowCount, columnProfiles);
    }

    private ColumnProfileDto profileColumn(DatasetColumn metadata, Column<?> column, long rowCount) {
        long missing = column.countMissing();
        double missingPct = rowCount == 0 ? 0.0 : (missing * 100.0) / rowCount;

        // Count distinct non-missing values ourselves so the semantics are consistent
        // across types (Tablesaw's countUnique() also counts the missing bucket).
        Map<String, Long> valueCounts = valueCounts(column);
        long unique = valueCounts.size();

        NumericStatsDto numericStats = null;
        List<CategoryCountDto> topCategories = null;
        if (column instanceof NumericColumn<?> numeric && !numeric.isEmpty()) {
            numericStats = new NumericStatsDto(
                    numeric.min(),
                    numeric.max(),
                    round2(numeric.mean()),
                    round2(numeric.median()),
                    round2(numeric.standardDeviation()));
        } else {
            topCategories = topCategories(valueCounts);
        }

        return new ColumnProfileDto(
                metadata.getName(),
                metadata.getColumnType(),
                missing,
                round2(missingPct),
                unique,
                numericStats,
                topCategories);
    }

    /** Counts occurrences of each non-missing value (as its string form). */
    private Map<String, Long> valueCounts(Column<?> column) {
        Map<String, Long> counts = new HashMap<>();
        for (int row = 0; row < column.size(); row++) {
            if (!column.isMissing(row)) {
                counts.merge(column.getString(row), 1L, Long::sum);
            }
        }
        return counts;
    }

    private List<CategoryCountDto> topCategories(Map<String, Long> valueCounts) {
        return valueCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(TOP_CATEGORIES)
                .map(e -> new CategoryCountDto(e.getKey(), e.getValue()))
                .toList();
    }

    private ColumnProfileDto emptyProfile(DatasetColumn metadata) {
        return new ColumnProfileDto(
                metadata.getName(), metadata.getColumnType(), 0, 0.0, 0, null, List.of());
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
