# DataLab Studio

A Java-based personal data analysis, visualization, and reporting platform.

DataLab Studio lets you import datasets, inspect data quality, profile and clean
data, build simple visualizations, save analysis workflows, and export reports.
It is built as a **JavaFX desktop client** backed by a **local Spring Boot
service** with **PostgreSQL** for metadata.

> **Status:** Phase 1 — foundation and a working vertical slice
> (CSV import → store → infer schema → profile → preview). See
> [docs/development-roadmap.md](docs/development-roadmap.md).

## Architecture

A hybrid desktop + local-service application:

- **`apps/desktop`** — JavaFX UI. Talks to the API over HTTP; holds no DB connection.
- **`apps/api`** — Spring Boot REST backend. Single writer of all persistent state.
- **`packages/shared`** — DTO contracts, enums, and constants shared by both apps.

Metadata lives in PostgreSQL; raw dataset files live on local disk under
`DATALAB_DATA_DIR`. See [docs/architecture.md](docs/architecture.md) for details.

## Tech stack

Java 21 · Maven (multi-module) · JavaFX · Spring Boot 3 (Web, Data JPA, Validation)
· PostgreSQL · Flyway · Tablesaw · XChart · Jackson · Apache Commons CSV · Apache POI
· SLF4J/Logback · JUnit 5 · Mockito · Testcontainers · Docker Compose.

## Prerequisites

- JDK 21
- Maven 3.9+ (or use the bundled `./mvnw` wrapper)
- Docker + Docker Compose (for PostgreSQL and integration tests)

## Quick start

```bash
# 1. Configure environment
cp .env.example .env

# 2. Start PostgreSQL
docker compose up -d

# 3. Build everything (runs unit tests; integration tests need Docker)
./mvnw clean install

# 4. Run the backend API
./mvnw -pl apps/api spring-boot:run
#    Health check:
curl http://localhost:8080/api/v1/health

# 5. Run the desktop client (in another terminal)
./mvnw -pl apps/desktop javafx:run
```

## Common commands

```bash
# Build / test a single module
./mvnw -pl apps/api clean test
./mvnw -pl packages/shared clean install

# Run a single test class or method
./mvnw -pl apps/api test -Dtest=CsvImportServiceTest
./mvnw -pl apps/api test -Dtest=DatasetServiceTest#importsCsvAndInfersTypes

# Run integration tests (Testcontainers; requires Docker)
./mvnw -pl apps/api verify
```

### Testing notes

- Unit tests run in the standard `test` phase. Integration tests are named
  `*IT` and run in `verify` (failsafe).
- `DatasetRepositoryIT` uses Testcontainers and **skips automatically** when no
  usable Docker environment is found, so the build stays green offline.
- `ApiClientLiveTest` (desktop) exercises the real HTTP client; it is skipped
  unless `DATALAB_API_BASE_URL` is set and the backend is reachable, e.g.:
  ```bash
  DATALAB_API_BASE_URL=http://localhost:8080 ./mvnw -pl apps/desktop test -Dtest=ApiClientLiveTest
  ```

## API (Phase 1)

| Method | Path                            | Purpose                  |
|--------|---------------------------------|--------------------------|
| GET    | `/api/v1/health`                | Liveness check           |
| POST   | `/api/v1/datasets/import/csv`   | Import a CSV (multipart) |
| GET    | `/api/v1/datasets`              | List datasets            |
| GET    | `/api/v1/datasets/{id}`         | Get one dataset          |
| GET    | `/api/v1/datasets/{id}/preview` | Preview first rows       |
| GET    | `/api/v1/datasets/{id}/profile` | Profiling summary        |

Example import:

```bash
curl -F "file=@sample.csv" http://localhost:8080/api/v1/datasets/import/csv
```

## Documentation

- [Architecture](docs/architecture.md)
- [Development roadmap](docs/development-roadmap.md)
- API design, database schema, and desktop UI flow docs are added as the
  corresponding features land (see `docs/`).

## Project layout

```
datalab-studio/
├── apps/
│   ├── api/         # Spring Boot backend
│   └── desktop/     # JavaFX client
├── packages/
│   └── shared/      # shared DTOs, enums, constants
├── docs/
├── docker-compose.yml
├── .env.example
└── pom.xml
```
