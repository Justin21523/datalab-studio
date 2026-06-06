package com.justin.datalab.desktop.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Builds a {@code multipart/form-data} request body for file uploads, since the
 * JDK {@link java.net.http.HttpClient} has no built-in multipart support.
 */
final class MultipartBody {

    private final String boundary = "----DataLabBoundary" + UUID.randomUUID().toString().replace("-", "");
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    String contentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    MultipartBody addField(String name, String value) {
        if (value == null) {
            return this;
        }
        write("--" + boundary + "\r\n");
        write("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
        write(value + "\r\n");
        return this;
    }

    MultipartBody addFile(String name, Path file, String contentType) {
        try {
            String fileName = file.getFileName().toString();
            write("--" + boundary + "\r\n");
            write("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"\r\n");
            write("Content-Type: " + contentType + "\r\n\r\n");
            buffer.write(Files.readAllBytes(file));
            write("\r\n");
            return this;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file for upload: " + file, e);
        }
    }

    byte[] build() {
        write("--" + boundary + "--\r\n");
        return buffer.toByteArray();
    }

    private void write(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        buffer.write(bytes, 0, bytes.length);
    }
}
