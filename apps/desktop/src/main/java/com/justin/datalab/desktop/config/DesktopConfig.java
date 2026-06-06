package com.justin.datalab.desktop.config;

/**
 * Desktop client configuration, resolved from environment variables with
 * sensible local-development defaults.
 */
public final class DesktopConfig {

    private final String apiBaseUrl;
    private final int previewLimit;

    private DesktopConfig(String apiBaseUrl, int previewLimit) {
        this.apiBaseUrl = apiBaseUrl;
        this.previewLimit = previewLimit;
    }

    public static DesktopConfig fromEnvironment() {
        String url = envOrDefault("DATALAB_API_BASE_URL", "http://localhost:8080");
        return new DesktopConfig(url, 100);
    }

    public String apiBaseUrl() {
        return apiBaseUrl;
    }

    public int previewLimit() {
        return previewLimit;
    }

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
