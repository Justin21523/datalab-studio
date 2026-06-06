package com.justin.datalab.dataset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Persistence access for {@link Dataset} aggregates.
 */
public interface DatasetRepository extends JpaRepository<Dataset, Long> {

    /** Fetches a dataset together with its columns in a single query. */
    @EntityGraph(attributePaths = "columns")
    Optional<Dataset> findWithColumnsById(Long id);

    /** Lists datasets newest-first (columns not fetched). */
    Page<Dataset> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
