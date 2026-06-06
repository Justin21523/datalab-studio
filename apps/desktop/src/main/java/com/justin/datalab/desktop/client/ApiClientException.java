package com.justin.datalab.desktop.client;

/**
 * Raised when an API call fails (transport error or non-success response).
 */
public class ApiClientException extends RuntimeException {

    public ApiClientException(String message) {
        super(message);
    }

    public ApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
