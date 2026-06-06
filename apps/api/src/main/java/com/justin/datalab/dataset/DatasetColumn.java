package com.justin.datalab.dataset;

import com.justin.datalab.shared.enums.ColumnType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A single column in a dataset's inferred schema.
 */
@Entity
@Table(name = "dataset_column")
public class DatasetColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(name = "column_type", nullable = false, length = 32)
    private ColumnType columnType;

    @Column(name = "is_nullable", nullable = false)
    private boolean nullable;

    protected DatasetColumn() {
        // Required by JPA.
    }

    public DatasetColumn(String name, int position, ColumnType columnType, boolean nullable) {
        this.name = name;
        this.position = position;
        this.columnType = columnType;
        this.nullable = nullable;
    }

    public Long getId() {
        return id;
    }

    public Dataset getDataset() {
        return dataset;
    }

    void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public boolean isNullable() {
        return nullable;
    }
}
