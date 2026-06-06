package com.justin.datalab.profiling;

import com.justin.datalab.dataset.Dataset;
import com.justin.datalab.dataset.DatasetColumn;
import com.justin.datalab.shared.dto.ColumnProfileDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;
import com.justin.datalab.storage.FileStorageService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Computes a Phase 1 profiling summary for a dataset: row/column counts plus,
 * per column, missing counts and distinct-value counts. Numeric statistics and
 * category frequencies are added in a later phase.
 */
@Service
public class ProfilingService {

    private final FileStorageService storage;

    public ProfilingService(FileStorageService storage) {
        this.storage = storage;
    }

    /**
     * Profiles a dataset by scanning its stored file once. The dataset's
     * columns must already be loaded.
     */
    public DatasetProfileDto profile(Dataset dataset) {
        List<DatasetColumn> columns = dataset.getColumns();
        int columnCount = columns.size();
        long[] missing = new long[columnCount];
        List<Set<String>> distinct = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            distinct.add(new HashSet<>());
        }

        long rowCount = 0;
        try (Reader reader = storage.newReader(dataset.getStoredPath());
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            Iterator<CSVRecord> it = parser.iterator();
            if (it.hasNext()) {
                it.next(); // skip header row
            }
            while (it.hasNext()) {
                CSVRecord record = it.next();
                rowCount++;
                for (int i = 0; i < columnCount; i++) {
                    String value = i < record.size() ? record.get(i) : "";
                    if (value == null || value.isBlank()) {
                        missing[i]++;
                    } else {
                        distinct.get(i).add(value);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to profile dataset " + dataset.getId(), e);
        }

        List<ColumnProfileDto> columnProfiles = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            DatasetColumn column = columns.get(i);
            double missingPct = rowCount == 0 ? 0.0 : (missing[i] * 100.0) / rowCount;
            columnProfiles.add(new ColumnProfileDto(
                    column.getName(),
                    column.getColumnType(),
                    missing[i],
                    round2(missingPct),
                    distinct.get(i).size()));
        }

        return new DatasetProfileDto(dataset.getId(), rowCount, columnCount, columnProfiles);
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
