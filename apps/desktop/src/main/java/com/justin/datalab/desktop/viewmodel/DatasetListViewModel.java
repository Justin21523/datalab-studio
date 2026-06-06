package com.justin.datalab.desktop.viewmodel;

import com.justin.datalab.desktop.service.DatasetApiService;
import com.justin.datalab.desktop.util.FxAsync;
import com.justin.datalab.shared.dto.DatasetDto;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;

/**
 * Presentation state for the dataset list screen. Holds the observable dataset
 * collection and drives refresh/import via the API service. Contains no UI code.
 */
public class DatasetListViewModel {

    private final DatasetApiService service;

    private final ObservableList<DatasetDto> datasets = FXCollections.observableArrayList();
    private final StringProperty status = new SimpleStringProperty("Ready");
    private final BooleanProperty busy = new SimpleBooleanProperty(false);

    public DatasetListViewModel(DatasetApiService service) {
        this.service = service;
    }

    public void refresh() {
        busy.set(true);
        status.set("Loading datasets...");
        FxAsync.handle(service.listDatasets(),
                list -> {
                    datasets.setAll(list);
                    status.set("Loaded " + list.size() + " dataset(s)");
                    busy.set(false);
                },
                error -> {
                    status.set("Failed to load datasets: " + error.getMessage());
                    busy.set(false);
                });
    }

    public void importCsv(Path file, String name) {
        busy.set(true);
        status.set("Importing " + file.getFileName() + "...");
        FxAsync.handle(service.importCsv(file, name),
                created -> {
                    status.set("Imported '" + created.name() + "' ("
                            + created.rowCount() + " rows)");
                    busy.set(false);
                    refresh();
                },
                error -> {
                    status.set("Import failed: " + error.getMessage());
                    busy.set(false);
                });
    }

    public ObservableList<DatasetDto> datasets() {
        return datasets;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public BooleanProperty busyProperty() {
        return busy;
    }
}
