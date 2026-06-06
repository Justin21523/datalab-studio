package com.justin.datalab.desktop.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Client-side mirror of the backend's response envelope. Only the fields the
 * desktop needs are mapped; everything else is ignored.
 *
 * @param <T> success payload type
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiEnvelope<T>(boolean success, T data, ApiErrorDto error) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiErrorDto(String code, String message) {
    }
}
