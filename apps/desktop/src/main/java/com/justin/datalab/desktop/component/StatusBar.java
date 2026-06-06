package com.justin.datalab.desktop.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Bottom status bar showing the latest message and a busy spinner.
 */
public class StatusBar extends HBox {

    private final Label messageLabel = new Label("Ready");
    private final ProgressIndicator busyIndicator = new ProgressIndicator();

    public StatusBar() {
        setPadding(new Insets(4, 8, 4, 8));
        setSpacing(8);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("status-bar");

        busyIndicator.setPrefSize(16, 16);
        busyIndicator.setVisible(false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(messageLabel, spacer, busyIndicator);
    }

    public void bindMessage(javafx.beans.value.ObservableValue<String> message) {
        messageLabel.textProperty().bind(message);
    }

    public void bindBusy(javafx.beans.value.ObservableValue<Boolean> busy) {
        busyIndicator.visibleProperty().bind(busy);
    }
}
