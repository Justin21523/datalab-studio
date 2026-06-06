package com.justin.datalab.desktop.util;

import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Bridges background {@link CompletableFuture} results back onto the JavaFX
 * application thread, so view models can update observable state safely.
 */
public final class FxAsync {

    private FxAsync() {
    }

    public static <T> void handle(CompletableFuture<T> future,
                                  Consumer<T> onSuccess,
                                  Consumer<Throwable> onError) {
        future.whenComplete((result, error) -> Platform.runLater(() -> {
            if (error != null) {
                onError.accept(unwrap(error));
            } else {
                onSuccess.accept(result);
            }
        }));
    }

    private static Throwable unwrap(Throwable error) {
        return (error.getCause() != null) ? error.getCause() : error;
    }
}
