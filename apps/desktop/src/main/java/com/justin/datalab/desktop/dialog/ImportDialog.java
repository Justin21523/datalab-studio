package com.justin.datalab.desktop.dialog;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Two-step CSV import flow: pick a file, then confirm the dataset name.
 * Returns the chosen file and name, or empty if the user cancels.
 */
public class ImportDialog {

    private final Window owner;

    public ImportDialog(Window owner) {
        this.owner = owner;
    }

    public Optional<Result> show() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select a CSV file to import");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        java.io.File file = chooser.showOpenDialog(owner);
        if (file == null) {
            return Optional.empty();
        }
        return askName(file.toPath());
    }

    private Optional<Result> askName(Path file) {
        Dialog<Result> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("Import dataset");
        dialog.setHeaderText("Confirm the dataset name");

        ButtonType importButton = new ButtonType("Import", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(importButton, ButtonType.CANCEL);

        TextField nameField = new TextField(stripExtension(file.getFileName().toString()));
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.addRow(0, new Label("File:"), new Label(file.toString()));
        grid.addRow(1, new Label("Name:"), nameField);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == importButton) {
                String name = nameField.getText().isBlank() ? null : nameField.getText().trim();
                return new Result(file, name);
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }

    /**
     * Outcome of the import dialog.
     *
     * @param file the selected CSV file
     * @param name the dataset name (may be {@code null} to let the server derive it)
     */
    public record Result(Path file, String name) {
    }
}
