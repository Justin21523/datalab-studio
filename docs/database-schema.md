# Database Schema

PostgreSQL stores **metadata only**. Raw dataset files live on local disk under
`DATALAB_DATA_DIR`; the `dataset.stored_path` column points to them.

The schema is owned by **Flyway** (`apps/api/src/main/resources/db/migration`).
Hibernate runs in `validate` mode and never alters the schema.

## `dataset`

One row per imported dataset.

| Column               | Type          | Notes                                  |
|----------------------|---------------|----------------------------------------|
| `id`                 | BIGINT (PK)   | Identity                               |
| `name`               | VARCHAR(255)  | User-facing name                       |
| `original_file_name` | VARCHAR(512)  | Uploaded file name                     |
| `stored_path`        | VARCHAR(1024) | Path relative to `DATALAB_DATA_DIR`    |
| `status`             | VARCHAR(32)   | `IMPORTING` / `READY` / `FAILED`       |
| `row_count`          | BIGINT        | Data rows (excludes header)            |
| `column_count`       | INTEGER       | Number of columns                      |
| `file_size_bytes`    | BIGINT        | Stored file size                       |
| `created_at`         | TIMESTAMPTZ   | Set on insert                          |
| `updated_at`         | TIMESTAMPTZ   | Set on insert/update                   |

Index: `idx_dataset_created_at (created_at DESC)` — supports newest-first listing.

## `dataset_column`

Inferred schema; one row per column, ordered by `position`.

| Column        | Type         | Notes                                            |
|---------------|--------------|--------------------------------------------------|
| `id`          | BIGINT (PK)  | Identity                                          |
| `dataset_id`  | BIGINT (FK)  | → `dataset(id)` `ON DELETE CASCADE`              |
| `name`        | VARCHAR(255) | Column header                                     |
| `position`    | INTEGER      | Zero-based index                                  |
| `column_type` | VARCHAR(32)  | `STRING/INTEGER/DECIMAL/BOOLEAN/DATE/DATETIME/UNKNOWN` |
| `is_nullable` | BOOLEAN      | True if any sampled value was missing             |

Constraints/indexes:
- `uq_dataset_column_position UNIQUE (dataset_id, position)`
- `idx_dataset_column_dataset_id (dataset_id)`

## Relationships

```
dataset 1 ──< dataset_column
        (cascade delete)
```

## Migrations

| Version | File           | Description           |
|---------|----------------|-----------------------|
| V1      | `V1__init.sql` | Initial schema        |

Add new migrations as `V2__*.sql`, `V3__*.sql`, … Never edit an applied migration.
