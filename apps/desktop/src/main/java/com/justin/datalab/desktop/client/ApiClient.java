package com.justin.datalab.desktop.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.justin.datalab.shared.constant.ApiPaths;
import com.justin.datalab.shared.dto.DatasetDto;
import com.justin.datalab.shared.dto.DatasetPreviewDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Synchronous HTTP client for the DataLab API, built on the JDK
 * {@link HttpClient} and Jackson. Calls are blocking; callers should invoke
 * them off the JavaFX application thread (see the service layer).
 */
public class ApiClient {

    private final String baseUrl;
    private final HttpClient http;
    private final ObjectMapper mapper;

    public ApiClient(String baseUrl) {
        this.baseUrl = stripTrailingSlash(baseUrl);
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /** Returns the reported service status (e.g. {@code "UP"}). */
    public String health() {
        JavaType type = mapper.getTypeFactory()
                .constructMapType(Map.class, String.class, String.class);
        Map<String, String> data = get(ApiPaths.HEALTH, type);
        return data.getOrDefault("status", "UNKNOWN");
    }

    public List<DatasetDto> listDatasets() {
        JavaType pageType = mapper.getTypeFactory()
                .constructParametricType(PageDto.class, DatasetDto.class);
        PageDto<DatasetDto> page = get(ApiPaths.DATASETS, pageType);
        return page.content();
    }

    public DatasetDto getDataset(long id) {
        return get(ApiPaths.DATASETS + "/" + id,
                mapper.getTypeFactory().constructType(DatasetDto.class));
    }

    public DatasetPreviewDto preview(long id, int limit) {
        String path = ApiPaths.DATASETS + "/" + id + ApiPaths.DATASET_PREVIEW + "?limit=" + limit;
        return get(path, mapper.getTypeFactory().constructType(DatasetPreviewDto.class));
    }

    public DatasetProfileDto profile(long id) {
        String path = ApiPaths.DATASETS + "/" + id + ApiPaths.DATASET_PROFILE;
        return get(path, mapper.getTypeFactory().constructType(DatasetProfileDto.class));
    }

    public DatasetDto importCsv(Path csvFile, String name) {
        MultipartBody body = new MultipartBody()
                .addField("name", name)
                .addFile("file", csvFile, "text/csv");
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + ApiPaths.DATASETS_IMPORT_CSV))
                .header("Content-Type", body.contentType())
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.build()))
                .build();
        return send(request, mapper.getTypeFactory().constructType(DatasetDto.class));
    }

    private <T> T get(String path, JavaType dataType) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Accept", "application/json")
                .GET()
                .build();
        return send(request, dataType);
    }

    private <T> T send(HttpRequest request, JavaType dataType) {
        HttpResponse<byte[]> response;
        try {
            response = http.send(request, BodyHandlers.ofByteArray());
        } catch (IOException e) {
            throw new ApiClientException("Cannot reach API at " + baseUrl + " (" + e.getMessage() + ")", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiClientException("Request interrupted", e);
        }

        JavaType envelopeType = mapper.getTypeFactory()
                .constructParametricType(ApiEnvelope.class, dataType);
        try {
            ApiEnvelope<T> envelope = mapper.readValue(response.body(), envelopeType);
            if (response.statusCode() / 100 != 2 || !envelope.success()) {
                throw new ApiClientException(describeError(envelope, response.statusCode()));
            }
            return envelope.data();
        } catch (IOException e) {
            throw new ApiClientException(
                    "Unexpected response (HTTP " + response.statusCode() + ")", e);
        }
    }

    private String describeError(ApiEnvelope<?> envelope, int statusCode) {
        if (envelope != null && envelope.error() != null && envelope.error().message() != null) {
            return envelope.error().message();
        }
        return "Request failed with HTTP " + statusCode;
    }

    private static String stripTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
