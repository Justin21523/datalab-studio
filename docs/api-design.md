# API Design

Base path: `/api/v1`. All responses use a uniform envelope.

## Response envelope

Success:

```json
{
  "success": true,
  "data": { "...": "..." },
  "timestamp": "2026-06-06T14:17:59.991Z"
}
```

Failure:

```json
{
  "success": false,
  "error": {
    "code": "NOT_FOUND",
    "message": "Dataset with id 999 not found",
    "details": [
      { "field": "name", "message": "must not be blank" }
    ]
  },
  "timestamp": "2026-06-06T14:18:00.095Z"
}
```

`data` is omitted on failure; `error` is omitted on success. `details` is present
only for validation failures. Error codes: `NOT_FOUND`, `BAD_REQUEST`,
`VALIDATION_FAILED`, `UPLOAD_TOO_LARGE`, `INTERNAL_ERROR`.

## Endpoints

### `GET /api/v1/health`

Liveness check.

```json
{ "success": true, "data": { "status": "UP", "service": "datalab-api" } }
```

### `POST /api/v1/datasets/import/csv`

Imports a CSV file. `multipart/form-data`.

| Part   | Type   | Required | Description                          |
|--------|--------|----------|--------------------------------------|
| `file` | file   | yes      | The CSV file to import               |
| `name` | text   | no       | Dataset name (defaults to file name) |

Returns `201 Created` with the dataset (including inferred columns).

```bash
curl -F "file=@sample.csv" -F "name=Sample Sales" \
  http://localhost:8080/api/v1/datasets/import/csv
```

### `GET /api/v1/datasets`

Lists datasets, newest first. Supports paging via `?page=`, `?size=`.
`data` is a page object; each item omits `columns`.

```json
{
  "success": true,
  "data": {
    "content": [
      { "id": 1, "name": "Sample Sales", "status": "READY",
        "rowCount": 4, "columnCount": 5, "columns": null }
    ],
    "page": 0, "size": 20, "totalElements": 1, "totalPages": 1, "last": true
  }
}
```

### `GET /api/v1/datasets/{id}`

Returns one dataset including its column schema. `404` if not found.

```json
{
  "success": true,
  "data": {
    "id": 1, "name": "Sample Sales", "originalFileName": "sample.csv",
    "status": "READY", "rowCount": 4, "columnCount": 5, "fileSizeBytes": 134,
    "createdAt": "2026-06-06T14:17:59.964Z",
    "columns": [
      { "id": 1, "name": "id", "position": 0, "type": "INTEGER", "nullable": false },
      { "id": 2, "name": "name", "position": 1, "type": "STRING", "nullable": true }
    ]
  }
}
```

### `GET /api/v1/datasets/{id}/preview?limit=N`

Returns up to `N` rows (default from `datalab.storage.preview-rows`).

```json
{
  "success": true,
  "data": {
    "datasetId": 1,
    "columns": ["id", "name", "score", "active", "joined"],
    "rows": [["1", "Alice", "9.5", "true", "2020-01-01"]],
    "returnedRows": 1, "totalRows": 4
  }
}
```

### `GET /api/v1/datasets/{id}/profile`

Returns a profiling summary.

```json
{
  "success": true,
  "data": {
    "datasetId": 1, "rowCount": 4, "columnCount": 5,
    "columns": [
      { "column": "name", "type": "STRING",
        "missingCount": 1, "missingPercentage": 25.0, "uniqueCount": 3 }
    ]
  }
}
```

## Conventions

- DTOs only — JPA entities are never serialized.
- Constructor injection throughout; controllers delegate to services.
- All errors flow through `GlobalExceptionHandler` to keep the envelope consistent.
- Timestamps are ISO-8601 (`Instant`), not epoch numbers.
