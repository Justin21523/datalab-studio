package com.justin.datalab.common.exception;

/**
 * Thrown when a requested resource does not exist. Mapped to HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Convenience for the common "&lt;type&gt; with id &lt;id&gt; not found" message.
     */
    public static ResourceNotFoundException of(String resourceType, Object id) {
        return new ResourceNotFoundException(resourceType + " with id " + id + " not found");
    }
}
