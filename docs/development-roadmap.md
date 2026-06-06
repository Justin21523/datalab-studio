# Development Roadmap

The project is built in phases. Each phase produces a working, demonstrable slice
rather than partially-wired features.

## Phase 1 — Foundation & vertical slice (current)

Goal: a working end-to-end path from CSV import to preview/profile in the desktop UI.

- [x] Multi-module Maven project (`api`, `desktop`, `shared`)
- [x] Docker Compose with PostgreSQL, `.env.example`
- [x] Backend common response envelope + global exception handling
- [x] Flyway initial migration
- [x] Dataset module: entity, column entity, status enum, repository, service, controller, DTOs
- [x] Local file storage service
- [x] CSV import: header parsing, simple type inference, schema persistence, preview rows
- [x] Profiling skeleton: row/column counts, missing counts, unique counts
- [x] JavaFX UI: main layout, dataset list, import dialog, preview view, status bar
- [x] Tests: dataset service unit, CSV import, repository integration (Testcontainers)

**Phase 1 API**

| Method | Path                                | Purpose                       |
|--------|-------------------------------------|-------------------------------|
| GET    | `/api/v1/health`                    | Liveness check                |
| POST   | `/api/v1/datasets/import/csv`       | Import a CSV (multipart)      |
| GET    | `/api/v1/datasets`                  | List datasets                 |
| GET    | `/api/v1/datasets/{id}`             | Get one dataset               |
| GET    | `/api/v1/datasets/{id}/preview`     | Preview first rows            |
| GET    | `/api/v1/datasets/{id}/profile`     | Profiling summary             |

## Phase 2 — Profiling depth (current)

- [x] Numeric stats: min, max, mean, median, standard deviation
- [x] Category frequency distributions (top-N per categorical column)
- [x] Duplicate row detection
- [x] Back profiling with Tablesaw
- [x] Surface the above in the desktop Profile tab

## Phase 3 — Cleaning pipeline

- Steps: remove missing rows, fill (mean/median/mode/fixed), rename, drop,
  convert type, filter rows, remove duplicates
- Reproducible, persisted pipeline definitions
- Apply pipeline → produce derived dataset

## Phase 4 — Visualization

- Bar, line, pie, scatter (XChart)
- Histogram, correlation heatmap (later)
- Export chart images

## Phase 5 — Reporting

- Export cleaned data as CSV
- Export profiling summary as JSON
- HTML report export, then PDF (OpenPDF / PDFBox)

## Phase 6 — Polish

- User settings (data directory, theme, default import options)
- Analysis history views
- GitHub Actions CI
- Optional: Spring Batch, Spark local mode, Smile/Tribuo ML

## Out of scope for Phase 1

Authentication, JSON/Excel import wiring (POI is present but not exposed yet),
advanced profiling, cleaning, visualization, and reporting.
