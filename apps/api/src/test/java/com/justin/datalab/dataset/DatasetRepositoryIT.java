package com.justin.datalab.dataset;

import com.justin.datalab.shared.enums.ColumnType;
import com.justin.datalab.shared.enums.DatasetStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the Flyway schema and JPA mappings against a real PostgreSQL
 * instance. Hibernate runs in {@code validate} mode, so this also confirms the
 * entities match the migration.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@EnabledIf("dockerAvailable")
class DatasetRepositoryIT {

    /**
     * Skips this integration test (rather than failing the build) when no usable
     * Docker environment is present. Evaluated before any container is started.
     */
    static boolean dockerAvailable() {
        return DockerClientFactory.instance().isDockerAvailable();
    }

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private DatasetRepository repository;

    @Test
    void persistsDatasetWithColumnsAndFetchesThemBack() {
        Dataset dataset = new Dataset("sales", "sales.csv", "datasets/x/sales.csv", DatasetStatus.READY);
        dataset.setRowCount(100);
        dataset.setColumnCount(2);
        dataset.setFileSizeBytes(2048);
        dataset.addColumn(new DatasetColumn("id", 0, ColumnType.INTEGER, false));
        dataset.addColumn(new DatasetColumn("region", 1, ColumnType.STRING, true));

        Dataset saved = repository.saveAndFlush(dataset);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        Optional<Dataset> reloaded = repository.findWithColumnsById(saved.getId());
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getColumns())
                .hasSize(2)
                .extracting(DatasetColumn::getName)
                .containsExactly("id", "region"); // ordered by position
    }

    @Test
    void listsDatasetsNewestFirst() {
        repository.saveAndFlush(
                new Dataset("first", "a.csv", "datasets/a/a.csv", DatasetStatus.READY));
        repository.saveAndFlush(
                new Dataset("second", "b.csv", "datasets/b/b.csv", DatasetStatus.READY));

        var page = repository.findAllByOrderByCreatedAtDesc(
                org.springframework.data.domain.PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(Dataset::getName).contains("first", "second");
    }
}
