package com.justin.datalab.desktop.controller;

import com.justin.datalab.desktop.component.DatasetListView;
import com.justin.datalab.desktop.component.DatasetPreviewView;
import com.justin.datalab.desktop.component.StatusBar;
import com.justin.datalab.desktop.dialog.ImportDialog;
import com.justin.datalab.desktop.viewmodel.DatasetDetailViewModel;
import com.justin.datalab.desktop.viewmodel.DatasetListViewModel;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

/**
 * Builds and wires the main application layout (toolbar + dataset list +
 * detail view + status bar). Holds no business logic — it delegates to the
 * view models, which call the API service.
 */
public class MainController {

    private final DatasetListViewModel listViewModel;
    private final DatasetDetailViewModel detailViewModel;

    private final BorderPane root = new BorderPane();

    public MainController(DatasetListViewModel listViewModel,
                          DatasetDetailViewModel detailViewModel) {
        this.listViewModel = listViewModel;
        this.detailViewModel = detailViewModel;
        buildLayout();
    }

    public BorderPane getRoot() {
        return root;
    }

    /** Triggers the initial data load. */
    public void start() {
        listViewModel.refresh();
    }

    private void buildLayout() {
        root.setTop(buildToolBar());

        DatasetListView listView = new DatasetListView(listViewModel.datasets());
        DatasetPreviewView previewView = new DatasetPreviewView(
                detailViewModel.previewProperty(), detailViewModel.profileProperty());

        listView.selectedDatasetProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                detailViewModel.load(selected.id());
            } else {
                detailViewModel.clear();
            }
        });

        SplitPane split = new SplitPane(listView, previewView);
        split.setOrientation(Orientation.HORIZONTAL);
        split.setDividerPositions(0.45);
        root.setCenter(split);

        StatusBar statusBar = new StatusBar();
        statusBar.bindMessage(listViewModel.statusProperty());
        statusBar.bindBusy(listViewModel.busyProperty());
        root.setBottom(statusBar);
    }

    private ToolBar buildToolBar() {
        Button importButton = new Button("Import CSV");
        importButton.setOnAction(e -> onImport());

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> listViewModel.refresh());

        return new ToolBar(importButton, refreshButton);
    }

    private void onImport() {
        new ImportDialog(root.getScene() == null ? null : root.getScene().getWindow())
                .show()
                .ifPresent(result -> listViewModel.importCsv(result.file(), result.name()));
    }
}
