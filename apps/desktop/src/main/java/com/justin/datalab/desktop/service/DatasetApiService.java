package com.justin.datalab.desktop.service;

import com.justin.datalab.desktop.client.ApiClient;
import com.justin.datalab.shared.dto.DatasetDto;
import com.justin.datalab.shared.dto.DatasetPreviewDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Async facade over {@link ApiClient}. Each call runs on a background thread so
 * the JavaFX application thread is never blocked on network I/O.
 */
public class DatasetApiService {

    private final ApiClient client;
    private final Executor executor;

    public DatasetApiService(ApiClient client) {
        this.client = client;
        this.executor = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "datalab-api-call");
            thread.setDaemon(true);
            return thread;
        });
    }

    public CompletableFuture<String> health() {
        return CompletableFuture.supplyAsync(client::health, executor);
    }

    public CompletableFuture<List<DatasetDto>> listDatasets() {
        return CompletableFuture.supplyAsync(client::listDatasets, executor);
    }

    public CompletableFuture<DatasetDto> importCsv(Path file, String name) {
        return CompletableFuture.supplyAsync(() -> client.importCsv(file, name), executor);
    }

    public CompletableFuture<DatasetPreviewDto> preview(long id, int limit) {
        return CompletableFuture.supplyAsync(() -> client.preview(id, limit), executor);
    }

    public CompletableFuture<DatasetProfileDto> profile(long id) {
        return CompletableFuture.supplyAsync(() -> client.profile(id), executor);
    }
}
