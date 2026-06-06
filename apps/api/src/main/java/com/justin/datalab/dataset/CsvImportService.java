package com.justin.datalab.dataset;

import com.justin.datalab.common.exception.BadRequestException;
import com.justin.datalab.shared.enums.ColumnType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parses CSV content: reads headers, infers column types, counts rows, and
 * produces preview rows. Stateless — callers provide a fresh reader each time.
 */
@Service
public class CsvImportService {

    private static final CSVFormat FORMAT = CSVFormat.DEFAULT;

    /**
     * Scans the entire file once to infer the schema and count data rows.
     */
    public CsvAnalysis analyze(Reader reader) {
        try (CSVParser parser = FORMAT.parse(reader)) {
            Iterator<CSVRecord> it = parser.iterator();
            if (!it.hasNext()) {
                throw new BadRequestException("CSV file is empty");
            }

            List<String> headers = toHeaderNames(it.next());
            int columnCount = headers.size();
            ColumnTypeInferer[] inferers = new ColumnTypeInferer[columnCount];
            for (int i = 0; i < columnCount; i++) {
                inferers[i] = new ColumnTypeInferer();
            }

            long rowCount = 0;
            while (it.hasNext()) {
                CSVRecord record = it.next();
                rowCount++;
                for (int i = 0; i < columnCount; i++) {
                    inferers[i].observe(i < record.size() ? record.get(i) : "");
                }
            }

            List<ColumnAnalysis> columns = new ArrayList<>(columnCount);
            for (int i = 0; i < columnCount; i++) {
                columns.add(new ColumnAnalysis(
                        headers.get(i),
                        inferers[i].resolveType(),
                        inferers[i].sawMissing()));
            }
            return new CsvAnalysis(columns, rowCount);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to parse CSV", e);
        }
    }

    /**
     * Reads up to {@code limit} data rows for previewing.
     */
    public PreviewData readPreview(Reader reader, int limit) {
        try (CSVParser parser = FORMAT.parse(reader)) {
            Iterator<CSVRecord> it = parser.iterator();
            if (!it.hasNext()) {
                throw new BadRequestException("CSV file is empty");
            }

            List<String> headers = toHeaderNames(it.next());
            int columnCount = headers.size();
            List<List<String>> rows = new ArrayList<>();
            while (it.hasNext() && rows.size() < limit) {
                CSVRecord record = it.next();
                List<String> row = new ArrayList<>(columnCount);
                for (int i = 0; i < columnCount; i++) {
                    row.add(i < record.size() ? record.get(i) : "");
                }
                rows.add(row);
            }
            return new PreviewData(headers, rows);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read CSV preview", e);
        }
    }

    private List<String> toHeaderNames(CSVRecord headerRecord) {
        List<String> names = new ArrayList<>(headerRecord.size());
        for (int i = 0; i < headerRecord.size(); i++) {
            String raw = headerRecord.get(i);
            names.add(raw == null || raw.isBlank() ? "column_" + (i + 1) : raw.trim());
        }
        return names;
    }

    /**
     * Inferred metadata for one column.
     */
    public record ColumnAnalysis(String name, ColumnType type, boolean nullable) {
    }

    /**
     * Result of analyzing a CSV file.
     */
    public record CsvAnalysis(List<ColumnAnalysis> columns, long rowCount) {
    }

    /**
     * Preview header and rows (as raw strings).
     */
    public record PreviewData(List<String> columns, List<List<String>> rows) {
    }
}
