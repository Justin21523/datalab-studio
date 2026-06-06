# Desktop UI Flow

The JavaFX client follows an **MVVM** structure. Controllers build the UI and
wire events; view models hold observable state and call services; the API
service runs all network I/O off the JavaFX application thread.

```
View (controller/component)  ──binds──>  ViewModel  ──calls──>  DatasetApiService
        ▲                                    │                        │
        └────────── FX thread ───────────────┘                        ▼
                 (FxAsync.handle)                                  ApiClient (HTTP)
```

## Screens (Phase 1)

### MainLayout (`controller/MainController`)

A `BorderPane`:
- **Top** — toolbar with *Import CSV* and *Refresh*.
- **Center** — a horizontal `SplitPane`:
  - left: `DatasetListView` (table of datasets)
  - right: `DatasetPreviewView` (tabs: Preview, Profile)
- **Bottom** — `StatusBar` (message + busy spinner).

### DatasetListView (`component/DatasetListView`)

A `TableView<DatasetDto>` bound to `DatasetListViewModel.datasets()`. Columns:
ID, Name, Rows, Columns, Status, File. Exposes the selected row.

### DatasetImportDialog (`dialog/ImportDialog`)

Two steps: a `FileChooser` (CSV filter), then a small dialog to confirm the
dataset name (pre-filled from the file name). Returns the file + name.

### DatasetPreviewView (`component/DatasetPreviewView`)

A `TabPane`:
- **Preview** — table with columns rebuilt dynamically from the preview header.
- **Profile** — table of per-column missing/unique metrics.

Reacts to `DatasetDetailViewModel`'s preview/profile properties.

### Status bar (`component/StatusBar`)

Shows the latest message (bound to the list view model's status) and a spinner
(bound to its busy flag).

## Interaction flows

**Startup**
1. `DataLabDesktopApplication` wires `ApiClient → DatasetApiService → view models → MainController`.
2. A health check runs; on success the dataset list loads, otherwise the status
   bar prompts the user to start the backend and click *Refresh*.

**Import**
1. *Import CSV* → `ImportDialog` → file + name.
2. `DatasetListViewModel.importCsv` uploads via the service, then refreshes the list.

**Inspect**
1. Selecting a row → `DatasetDetailViewModel.load(id)`.
2. Preview and profile are fetched concurrently and rendered in their tabs.

## Threading rule

Network calls return `CompletableFuture`s from `DatasetApiService`. UI updates
only happen inside `FxAsync.handle`, which marshals results back onto the FX
thread. Controllers and view models never block on I/O.

## Configuration

The API base URL comes from `DATALAB_API_BASE_URL` (default
`http://localhost:8080`). Run with:

```bash
DATALAB_API_BASE_URL=http://localhost:8080 ./mvnw -pl apps/desktop javafx:run
```
