package com.justin.datalab.profiling;

import com.justin.datalab.config.StorageProperties;
import com.justin.datalab.dataset.Dataset;
import com.justin.datalab.dataset.DatasetColumn;
import com.justin.datalab.shared.dto.ColumnProfileDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;
import com.justin.datalab.shared.enums.ColumnType;
import com.justin.datalab.shared.enums.DatasetStatus;
import com.justin.datalab.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ProfilingServiceTest {

    private static final String RELATIVE_PATH = "datasets/x/data.csv";

    private ProfilingService service;
    private Dataset dataset;

    @BeforeEach
    void setUp(@TempDir Path tmp) throws IOException {
        Path csv = tmp.resolve(RELATIVE_PATH);
        Files.createDirectories(csv.getParent());
        Files.writeString(csv, """
                id,city,score
                1,Taipei,10
                2,Tokyo,20
                3,Taipei,30
                3,Taipei,30
                """);

        FileStorageService storage = new FileStorageService(new StorageProperties(tmp.toString(), 50));
        service = new ProfilingService(storage);

        dataset = new Dataset("t", "data.csv", RELATIVE_PATH, DatasetStatus.READY);
        dataset.addColumn(new DatasetColumn("id", 0, ColumnType.INTEGER, false));
        dataset.addColumn(new DatasetColumn("city", 1, ColumnType.STRING, false));
        dataset.addColumn(new DatasetColumn("score", 2, ColumnType.INTEGER, false));
    }

    @Test
    void reportsRowAndDuplicateCounts() {
        DatasetProfileDto profile = service.profile(dataset);

        assertThat(profile.rowCount()).isEqualTo(4);
        assertThat(profile.duplicateRowCount()).isEqualTo(1); // the repeated "3,Taipei,30" row
        assertThat(profile.columns()).hasSize(3);
    }

    @Test
    void computesNumericStatsForNumericColumns() {
        DatasetProfileDto profile = service.profile(dataset);
        ColumnProfileDto score = profile.columns().get(2);

        assertThat(score.numericStats()).isNotNull();
        assertThat(score.numericStats().min()).isEqualTo(10.0);
        assertThat(score.numericStats().max()).isEqualTo(30.0);
        assertThat(score.numericStats().mean()).isEqualTo(22.5);
        assertThat(score.numericStats().median()).isBetween(20.0, 30.0);
        assertThat(score.numericStats().stddev()).isGreaterThan(0.0);
        assertThat(score.topCategories()).isNull();
        assertThat(score.missingCount()).isZero();
    }

    @Test
    void computesTopCategoriesForCategoricalColumns() {
        DatasetProfileDto profile = service.profile(dataset);
        ColumnProfileDto city = profile.columns().get(1);

        assertThat(city.numericStats()).isNull();
        assertThat(city.topCategories()).isNotNull();
        assertThat(city.topCategories().get(0).value()).isEqualTo("Taipei");
        assertThat(city.topCategories().get(0).count()).isEqualTo(3);
        assertThat(city.uniqueCount()).isEqualTo(2);
    }

    @Test
    void uniqueCountExcludesMissingValues(@TempDir Path tmp) throws IOException {
        Path csv = tmp.resolve("d.csv");
        Files.writeString(csv, """
                city,n
                Taipei,1
                Tokyo,2
                ,3
                Taipei,4
                """);
        ProfilingService svc = new ProfilingService(
                new FileStorageService(new StorageProperties(tmp.toString(), 50)));
        Dataset ds = new Dataset("t", "d.csv", "d.csv", DatasetStatus.READY);
        ds.addColumn(new DatasetColumn("city", 0, ColumnType.STRING, true));
        ds.addColumn(new DatasetColumn("n", 1, ColumnType.INTEGER, false));

        ColumnProfileDto city = svc.profile(ds).columns().get(0);

        assertThat(city.missingCount()).isEqualTo(1);
        assertThat(city.uniqueCount()).isEqualTo(2); // Taipei, Tokyo — blank excluded
    }
}
