package com.justin.datalab.desktop.viewmodel;

import com.justin.datalab.desktop.service.DatasetApiService;
import com.justin.datalab.desktop.util.FxAsync;
import com.justin.datalab.shared.dto.DatasetPreviewDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Presentation state for the dataset detail screen: the current preview and
 * profile for a selected dataset.
 */
public class DatasetDetailViewModel {

    private final DatasetApiService service;
    private final int previewLimit;

    private final ObjectProperty<DatasetPreviewDto> preview = new SimpleObjectProperty<>();
    private final ObjectProperty<DatasetProfileDto> profile = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();

    public DatasetDetailViewModel(DatasetApiService service, int previewLimit) {
        this.service = service;
        this.previewLimit = previewLimit;
    }

    /** Loads preview and profile for the given dataset. */
    public void load(long datasetId) {
        status.set("Loading dataset #" + datasetId + "...");
        FxAsync.handle(service.preview(datasetId, previewLimit),
                preview::set,
                error -> status.set("Failed to load preview: " + error.getMessage()));
        FxAsync.handle(service.profile(datasetId),
                result -> {
                    profile.set(result);
                    status.set("Loaded dataset #" + datasetId);
                },
                error -> status.set("Failed to load profile: " + error.getMessage()));
    }

    public void clear() {
        preview.set(null);
        profile.set(null);
    }

    public ObjectProperty<DatasetPreviewDto> previewProperty() {
        return preview;
    }

    public ObjectProperty<DatasetProfileDto> profileProperty() {
        return profile;
    }

    public StringProperty statusProperty() {
        return status;
    }
}
