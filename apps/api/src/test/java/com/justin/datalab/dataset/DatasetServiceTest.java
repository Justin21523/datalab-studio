package com.justin.datalab.dataset;

import com.justin.datalab.common.exception.ResourceNotFoundException;
import com.justin.datalab.config.StorageProperties;
import com.justin.datalab.dataset.CsvImportService.ColumnAnalysis;
import com.justin.datalab.dataset.CsvImportService.CsvAnalysis;
import com.justin.datalab.profiling.ProfilingService;
import com.justin.datalab.shared.dto.DatasetDto;
import com.justin.datalab.shared.enums.ColumnType;
import com.justin.datalab.shared.enums.DatasetStatus;
import com.justin.datalab.storage.FileStorageService;
import com.justin.datalab.storage.FileStorageService.StoredFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatasetServiceTest {

    @Mock
    private DatasetRepository repository;
    @Mock
    private FileStorageService storage;
    @Mock
    private CsvImportService csvImportService;
    @Mock
    private ProfilingService profilingService;

    // StorageProperties is a record, so it is supplied explicitly rather than mocked.
    private final StorageProperties storageProperties = new StorageProperties("./data", 50);

    private DatasetService serviceWithProperties() {
        return new DatasetService(repository, storage, csvImportService, profilingService, storageProperties);
    }

    @Test
    void importCsvStoresFileInfersSchemaAndPersists() {
        when(storage.store(any(InputStream.class), anyString()))
                .thenReturn(new StoredFile("datasets/abc/data.csv", 42L));
        when(storage.newReader("datasets/abc/data.csv"))
                .thenReturn(new BufferedReader(new StringReader("id,name\n1,Alice\n")));
        when(csvImportService.analyze(any()))
                .thenReturn(new CsvAnalysis(List.of(
                        new ColumnAnalysis("id", ColumnType.INTEGER, false),
                        new ColumnAnalysis("name", ColumnType.STRING, true)), 5L));
        when(repository.save(any(Dataset.class))).thenAnswer(inv -> inv.getArgument(0));

        DatasetService target = serviceWithProperties();
        InputStream content = new ByteArrayInputStream("id,name\n1,Alice\n".getBytes(StandardCharsets.UTF_8));

        DatasetDto result = target.importCsv(content, "data.csv", "My Dataset");

        assertThat(result.name()).isEqualTo("My Dataset");
        assertThat(result.status()).isEqualTo(DatasetStatus.READY);
        assertThat(result.rowCount()).isEqualTo(5L);
        assertThat(result.columnCount()).isEqualTo(2);
        assertThat(result.fileSizeBytes()).isEqualTo(42L);
        assertThat(result.columns()).hasSize(2);

        verify(storage).store(any(InputStream.class), anyString());
        verify(repository).save(any(Dataset.class));
    }

    @Test
    void getThrowsWhenDatasetMissing() {
        when(repository.findWithColumnsById(99L)).thenReturn(Optional.empty());

        DatasetService target = serviceWithProperties();

        assertThatThrownBy(() -> target.get(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
