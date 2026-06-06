package com.justin.datalab.desktop.component;

import com.justin.datalab.shared.dto.DatasetDto;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Table of datasets. Exposes the selected-row property so the surrounding
 * controller can react to selection changes.
 */
public class DatasetListView extends TableView<DatasetDto> {

    public DatasetListView(ObservableList<DatasetDto> items) {
        setItems(items);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        getColumns().add(column("ID", d -> String.valueOf(d.id())));
        getColumns().add(column("Name", DatasetDto::name));
        getColumns().add(column("Rows", d -> String.valueOf(d.rowCount())));
        getColumns().add(column("Columns", d -> String.valueOf(d.columnCount())));
        getColumns().add(column("Status", d -> d.status().name()));
        getColumns().add(column("File", DatasetDto::originalFileName));
    }

    public ReadOnlyObjectProperty<DatasetDto> selectedDatasetProperty() {
        return getSelectionModel().selectedItemProperty();
    }

    private TableColumn<DatasetDto, String> column(String title,
                                                  java.util.function.Function<DatasetDto, String> extractor) {
        TableColumn<DatasetDto, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cell -> new SimpleStringProperty(extractor.apply(cell.getValue())));
        return col;
    }
}
