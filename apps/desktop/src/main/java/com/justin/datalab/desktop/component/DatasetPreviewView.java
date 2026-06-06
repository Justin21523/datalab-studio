package com.justin.datalab.desktop.component;

import com.justin.datalab.shared.dto.CategoryCountDto;
import com.justin.datalab.shared.dto.ColumnProfileDto;
import com.justin.datalab.shared.dto.DatasetPreviewDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;
import com.justin.datalab.shared.dto.NumericStatsDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Tabbed detail view showing a dataset's preview rows and its profiling summary.
 * Rebuilds itself reactively when the bound preview/profile properties change.
 */
public class DatasetPreviewView extends TabPane {

    private final TableView<List<String>> previewTable = new TableView<>();
    private final TableView<ColumnProfileDto> profileTable = new TableView<>();
    private final Label profileSummary = new Label("Select a dataset to view its profile");

    public DatasetPreviewView(ObservableValue<DatasetPreviewDto> preview,
                              ObservableValue<DatasetProfileDto> profile) {
        previewTable.setPlaceholder(new Label("Select a dataset to preview its rows"));
        profileTable.setPlaceholder(new Label("Select a dataset to view its profile"));
        buildProfileColumns();

        Tab previewTab = new Tab("Preview", previewTable);
        previewTab.setClosable(false);
        Tab profileTab = new Tab("Profile", buildProfilePane());
        profileTab.setClosable(false);
        getTabs().addAll(previewTab, profileTab);

        preview.addListener((obs, old, value) -> renderPreview(value));
        profile.addListener((obs, old, value) -> renderProfile(value));
    }

    private VBox buildProfilePane() {
        profileSummary.setPadding(new Insets(6, 8, 6, 8));
        VBox pane = new VBox(profileSummary, profileTable);
        VBox.setVgrow(profileTable, Priority.ALWAYS);
        return pane;
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
            profileSummary.setText("Select a dataset to view its profile");
            profileTable.getItems().clear();
            return;
        }
        profileSummary.setText(String.format(
                "Rows: %d  ·  Columns: %d  ·  Duplicate rows: %d",
                profile.rowCount(), profile.columnCount(), profile.duplicateRowCount()));
        profileTable.setItems(FXCollections.observableArrayList(profile.columns()));
    }

    private void buildProfileColumns() {
        profileTable.getColumns().add(textColumn("Column", ColumnProfileDto::column));
        profileTable.getColumns().add(textColumn("Type", p -> p.type().name()));
        profileTable.getColumns().add(textColumn("Missing", p -> String.valueOf(p.missingCount())));
        profileTable.getColumns().add(textColumn("Missing %", p -> p.missingPercentage() + "%"));
        profileTable.getColumns().add(textColumn("Unique", p -> String.valueOf(p.uniqueCount())));
        profileTable.getColumns().add(numericColumn("Min", NumericStatsDto::min));
        profileTable.getColumns().add(numericColumn("Max", NumericStatsDto::max));
        profileTable.getColumns().add(numericColumn("Mean", NumericStatsDto::mean));
        profileTable.getColumns().add(numericColumn("Median", NumericStatsDto::median));
        profileTable.getColumns().add(numericColumn("StdDev", NumericStatsDto::stddev));
        profileTable.getColumns().add(textColumn("Top values", DatasetPreviewView::formatCategories));
    }

    private static String formatCategories(ColumnProfileDto profile) {
        List<CategoryCountDto> categories = profile.topCategories();
        if (categories == null || categories.isEmpty()) {
            return "";
        }
        return categories.stream()
                .limit(5)
                .map(c -> c.value() + " (" + c.count() + ")")
                .collect(Collectors.joining(", "));
    }

    private TableColumn<ColumnProfileDto, String> textColumn(
            String title, Function<ColumnProfileDto, String> extractor) {
        TableColumn<ColumnProfileDto, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cell -> new SimpleStringProperty(extractor.apply(cell.getValue())));
        return col;
    }

    private TableColumn<ColumnProfileDto, String> numericColumn(
            String title, ToDoubleFunction<NumericStatsDto> extractor) {
        return textColumn(title, profile -> {
            NumericStatsDto stats = profile.numericStats();
            return stats == null ? "" : String.valueOf(extractor.applyAsDouble(stats));
        });
    }
}
