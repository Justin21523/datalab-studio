package com.justin.datalab.desktop.component;

import com.justin.datalab.shared.dto.ColumnProfileDto;
import com.justin.datalab.shared.dto.DatasetPreviewDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Tabbed detail view showing a dataset's preview rows and its profiling summary.
 * Rebuilds itself reactively when the bound preview/profile properties change.
 */
public class DatasetPreviewView extends TabPane {

    private final TableView<List<String>> previewTable = new TableView<>();
    private final TableView<ColumnProfileDto> profileTable = new TableView<>();

    public DatasetPreviewView(ObservableValue<DatasetPreviewDto> preview,
                              ObservableValue<DatasetProfileDto> profile) {
        previewTable.setPlaceholder(new Label("Select a dataset to preview its rows"));
        profileTable.setPlaceholder(new Label("Select a dataset to view its profile"));
        buildProfileColumns();

        Tab previewTab = new Tab("Preview", previewTable);
        previewTab.setClosable(false);
        Tab profileTab = new Tab("Profile", profileTable);
        profileTab.setClosable(false);
        getTabs().addAll(previewTab, profileTab);

        preview.addListener((obs, old, value) -> renderPreview(value));
        profile.addListener((obs, old, value) -> renderProfile(value));
    }

    private void renderPreview(DatasetPreviewDto preview) {
        previewTable.getColumns().clear();
        if (preview == null) {
            previewTable.getItems().clear();
            return;
        }
        List<String> headers = preview.columns();
        for (int i = 0; i < headers.size(); i++) {
            final int index = i;
            TableColumn<List<String>, String> col = new TableColumn<>(headers.get(i));
            col.setCellValueFactory(cell -> {
                List<String> row = cell.getValue();
                return new SimpleStringProperty(index < row.size() ? row.get(index) : "");
            });
            previewTable.getColumns().add(col);
        }
        previewTable.setItems(FXCollections.observableArrayList(preview.rows()));
    }

    private void renderProfile(DatasetProfileDto profile) {
        if (profile == null) {
            profileTable.getItems().clear();
            return;
        }
        profileTable.setItems(FXCollections.observableArrayList(profile.columns()));
    }

    private void buildProfileColumns() {
        profileTable.getColumns().add(textColumn("Column", ColumnProfileDto::column));
        profileTable.getColumns().add(textColumn("Type", p -> p.type().name()));
        profileTable.getColumns().add(textColumn("Missing", p -> String.valueOf(p.missingCount())));
        profileTable.getColumns().add(textColumn("Missing %", p -> p.missingPercentage() + "%"));
        profileTable.getColumns().add(textColumn("Unique", p -> String.valueOf(p.uniqueCount())));
    }

    private TableColumn<ColumnProfileDto, String> textColumn(
            String title, java.util.function.Function<ColumnProfileDto, String> extractor) {
        TableColumn<ColumnProfileDto, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cell -> new SimpleStringProperty(extractor.apply(cell.getValue())));
        return col;
    }
}
