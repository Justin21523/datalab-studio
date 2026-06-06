package com.justin.datalab.desktop;

import com.justin.datalab.desktop.client.ApiClient;
import com.justin.datalab.desktop.config.DesktopConfig;
import com.justin.datalab.desktop.controller.MainController;
import com.justin.datalab.desktop.service.DatasetApiService;
import com.justin.datalab.desktop.util.FxAsync;
import com.justin.datalab.desktop.viewmodel.DatasetDetailViewModel;
import com.justin.datalab.desktop.viewmodel.DatasetListViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaFX entry point. Wires the API client, service, view models, and the main
 * controller, then shows the primary window.
 */
public class DataLabDesktopApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(DataLabDesktopApplication.class);

    @Override
    public void start(Stage stage) {
        DesktopConfig config = DesktopConfig.fromEnvironment();
        log.info("Starting DataLab Studio desktop against API {}", config.apiBaseUrl());

        ApiClient apiClient = new ApiClient(config.apiBaseUrl());
        DatasetApiService apiService = new DatasetApiService(apiClient);

        DatasetListViewModel listViewModel = new DatasetListViewModel(apiService);
        DatasetDetailViewModel detailViewModel =
                new DatasetDetailViewModel(apiService, config.previewLimit());

        MainController controller = new MainController(listViewModel, detailViewModel);

        Scene scene = new Scene(controller.getRoot(), 1100, 700);
        stage.setTitle("DataLab Studio");
        stage.setScene(scene);
        stage.show();

        // Confirm backend connectivity for early feedback, then load data.
        FxAsync.handle(apiService.health(),
                status -> {
                    listViewModel.statusProperty().set("Connected to API (status: " + status + ")");
                    controller.start();
                },
                error -> listViewModel.statusProperty()
                        .set("Cannot reach API at " + config.apiBaseUrl()
                                + " — start the backend, then click Refresh"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
