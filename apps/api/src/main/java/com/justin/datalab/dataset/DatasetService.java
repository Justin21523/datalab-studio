package com.justin.datalab.dataset;

import com.justin.datalab.common.exception.BadRequestException;
import com.justin.datalab.common.exception.ResourceNotFoundException;
import com.justin.datalab.common.pagination.PageResponse;
import com.justin.datalab.config.StorageProperties;
import com.justin.datalab.dataset.CsvImportService.ColumnAnalysis;
import com.justin.datalab.dataset.CsvImportService.CsvAnalysis;
import com.justin.datalab.dataset.CsvImportService.PreviewData;
import com.justin.datalab.profiling.ProfilingService;
import com.justin.datalab.shared.dto.DatasetDto;
import com.justin.datalab.shared.dto.DatasetPreviewDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;
import com.justin.datalab.shared.enums.DatasetStatus;
import com.justin.datalab.storage.FileStorageService;
import com.justin.datalab.storage.FileStorageService.StoredFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

/**
 * Orchestrates dataset import and read operations: storing the raw file,
 * parsing/profiling it, persisting metadata, and exposing it as DTOs.
 */
@Service
public class DatasetService {

    private static final Logger log = LoggerFactory.getLogger(DatasetService.class);

    private final DatasetRepository repository;
    private final FileStorageService storage;
    private final CsvImportService csvImportService;
    private final ProfilingService profilingService;
    private final StorageProperties storageProperties;

    public DatasetService(DatasetRepository repository,
                          FileStorageService storage,
                          CsvImportService csvImportService,
                          ProfilingService profilingService,
                          StorageProperties storageProperties) {
        this.repository = repository;
        this.storage = storage;
        this.csvImportService = csvImportService;
        this.profilingService = profilingService;
        this.storageProperties = storageProperties;
    }

    /**
     * Imports a CSV: stores the file, infers schema, counts rows, and persists
     * metadata. Returns the created dataset with its column schema.
     */
    @Transactional
    public DatasetDto importCsv(InputStream content, String originalFileName, String requestedName) {
        StoredFile stored = storage.store(content, originalFileName);
        String name = resolveName(requestedName, originalFileName);

        Dataset dataset = new Dataset(name, originalFileName, stored.relativePath(), DatasetStatus.IMPORTING);
        dataset.setFileSizeBytes(stored.sizeBytes());

        try {
            CsvAnalysis analysis = csvImportService.analyze(storage.newReader(stored.relativePath()));
            int position = 0;
            for (ColumnAnalysis column : analysis.columns()) {
                dataset.addColumn(new DatasetColumn(
                        column.name(), position++, column.type(), column.nullable()));
            }
            dataset.setRowCount(analysis.rowCount());
            dataset.setColumnCount(analysis.columns().size());
            dataset.setStatus(DatasetStatus.READY);
        } catch (BadRequestException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("CSV import failed for '{}'", originalFileName, e);
            throw new BadRequestException("Failed to import CSV: " + e.getMessage(), e);
        }

        Dataset saved = repository.save(dataset);
        log.info("Imported dataset id={} name='{}' rows={} columns={}",
                saved.getId(), saved.getName(), saved.getRowCount(), saved.getColumnCount());
        return DatasetMapper.toDetailDto(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<DatasetDto> list(Pageable pageable) {
        return PageResponse.of(
                repository.findAllByOrderByCreatedAtDesc(pageable),
                DatasetMapper::toSummaryDto);
    }

    @Transactional(readOnly = true)
    public DatasetDto get(Long id) {
        return DatasetMapper.toDetailDto(requireWithColumns(id));
    }

    @Transactional(readOnly = true)
    public DatasetPreviewDto preview(Long id, Integer limit) {
        Dataset dataset = requireExisting(id);
        int effectiveLimit = (limit == null || limit <= 0) ? storageProperties.previewRows() : limit;
        PreviewData data = csvImportService.readPreview(
                storage.newReader(dataset.getStoredPath()), effectiveLimit);
        return new DatasetPreviewDto(
                dataset.getId(),
                data.columns(),
                data.rows(),
                data.rows().size(),
                dataset.getRowCount());
    }

    @Transactional(readOnly = true)
    public DatasetProfileDto profile(Long id) {
        return profilingService.profile(requireWithColumns(id));
    }

    private Dataset requireExisting(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Dataset", id));
    }

    private Dataset requireWithColumns(Long id) {
        return repository.findWithColumnsById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Dataset", id));
    }

    private String resolveName(String requestedName, String originalFileName) {
        if (requestedName != null && !requestedName.isBlank()) {
            return requestedName.trim();
        }
        if (originalFileName == null || originalFileName.isBlank()) {
            return "dataset";
        }
        int dot = originalFileName.lastIndexOf('.');
        return dot > 0 ? originalFileName.substring(0, dot) : originalFileName;
    }
}
