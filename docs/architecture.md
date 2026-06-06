# Architecture

DataLab Studio is a personal data analysis, visualization, and reporting platform
built as a **hybrid desktop + local service** application.

## High-level view

```
+---------------------------+         HTTP/JSON          +---------------------------+
|      Desktop client       |  ───────────────────────▶  |     Backend API service   |
|        (JavaFX)           |  ◀───────────────────────  |       (Spring Boot)       |
|                           |                            |                           |
|  - import / preview UI    |                            |  - dataset metadata       |
|  - cleaning pipeline UI   |                            |  - profiling              |
|  - charts & reports       |                            |  - pipelines / reports    |
|  - JDK HttpClient + JSON  |                            |  - storage orchestration  |
+---------------------------+                            +-------------+-------------+
                                                                       |
                                                +----------------------+----------------------+
                                                |                                             |
                                       +--------v---------+                         +---------v---------+
                                       |   PostgreSQL     |                         |  Local file store  |
                                       |  (metadata only) |                         | (raw dataset files)|
                                       +------------------+                         +-------------------+
```

## Modules

| Module             | Artifact  | Responsibility                                                        |
|--------------------|-----------|----------------------------------------------------------------------|
| `apps/api`         | `api`     | Spring Boot REST backend. Single writer of all persistent state.     |
| `apps/desktop`     | `desktop` | JavaFX client. Talks to the API over HTTP; never touches the DB.     |
| `packages/shared`  | `shared`  | DTO contracts, enums, and constants shared by both apps.             |

## Backend package layout (`com.justin.datalab`)

```
com.justin.datalab
├── DataLabApplication.java
├── config            # cross-cutting Spring configuration (Jackson, CORS, properties)
├── common
│   ├── exception     # domain exceptions + GlobalExceptionHandler
│   ├── response      # ApiResponse envelope + ApiError
│   ├── pagination    # PageResponse
│   └── validation    # custom validation support (as needed)
├── dataset           # dataset import, metadata, preview
├── profiling         # data profiling
├── pipeline          # cleaning pipeline (later phase)
├── visualization     # chart generation (later phase)
├── report            # report export (later phase)
├── storage           # local file storage service
└── userconfig        # user settings (later phase)
```

## Desktop package layout (`com.justin.datalab.desktop`)

```
com.justin.datalab.desktop
├── DataLabDesktopApplication.java
├── config        # base URL / app configuration
├── controller    # FXML controllers (no business logic)
├── viewmodel     # observable state + presentation logic
├── service       # thin async wrappers over the API client
├── client        # ApiClient (JDK HttpClient + Jackson)
├── component     # reusable UI components
├── dialog        # modal dialogs (e.g. import)
└── util          # helpers (FX threading, etc.)
```

## Key design decisions

- **Data ownership split.** PostgreSQL stores *metadata only* (dataset records,
  column schema, profiling results). Raw dataset files live on disk under
  `DATALAB_DATA_DIR`. This keeps the DB small and import/preview fast.
- **The API is the single source of truth.** The desktop client is a pure
  consumer over HTTP and holds no database connection.
- **Layering is strict.** Controllers (both backend and JavaFX) contain no
  business logic; they delegate to services / view models. JPA entities never
  leave the service layer — responses are always DTOs.
- **Schema is owned by Flyway.** Hibernate runs in `validate` mode; all schema
  changes are versioned migrations.
- **Consistent API surface.** Every endpoint returns the same `ApiResponse`
  envelope, and all errors flow through a single `GlobalExceptionHandler`.

## Request flow: CSV import (Phase 1 vertical slice)

1. Desktop `DatasetImportDialog` selects a file and POSTs it as
   `multipart/form-data` to `POST /api/v1/datasets/import/csv`.
2. `DatasetController` delegates to `DatasetService`.
3. `FileStorageService` writes the raw file under `DATALAB_DATA_DIR/datasets/{id}/`.
4. `CsvImportService` parses headers, samples rows, infers column types.
5. `Dataset` + `DatasetColumn` rows are persisted via JPA.
6. A `DatasetResponse` DTO is returned in the `ApiResponse` envelope.
7. Preview and profile are computed on demand by reading the stored file.
