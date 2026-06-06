package com.justin.datalab.storage;

import com.justin.datalab.common.exception.BadRequestException;
import com.justin.datalab.config.StorageProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Stores raw dataset files on the local filesystem under a configured root
 * directory. PostgreSQL only keeps the relative {@code storedPath}; the bytes
 * live here.
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path baseDir;

    public FileStorageService(StorageProperties properties) {
        this.baseDir = Paths.get(properties.dataDir()).toAbsolutePath().normalize();
    }

    @PostConstruct
    void init() {
        try {
            Files.createDirectories(baseDir);
            log.info("File storage root: {}", baseDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create storage directory: " + baseDir, e);
        }
    }

    /**
     * Copies the given content into a fresh per-dataset directory.
     *
     * @return the relative path (forward-slashed) and stored size in bytes
     */
    public StoredFile store(InputStream content, String originalFileName) {
        String safeName = sanitize(originalFileName);
        String relativeDir = "datasets/" + UUID.randomUUID();
        Path targetDir = baseDir.resolve(relativeDir);
        Path target = targetDir.resolve(safeName);
        try {
            Files.createDirectories(targetDir);
            long bytes = Files.copy(content, target);
            String relativePath = relativeDir + "/" + safeName;
            log.info("Stored file '{}' ({} bytes) at {}", originalFileName, bytes, relativePath);
            return new StoredFile(relativePath, bytes);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file: " + originalFileName, e);
        }
    }

    /**
     * Opens a UTF-8 buffered reader over a previously stored file.
     * Callers are responsible for closing it.
     */
    public BufferedReader newReader(String relativePath) {
        try {
            return Files.newBufferedReader(resolve(relativePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read stored file: " + relativePath, e);
        }
    }

    /** Resolves a relative stored path to an absolute path, guarding against traversal. */
    public Path resolve(String relativePath) {
        Path resolved = baseDir.resolve(relativePath).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new BadRequestException("Invalid storage path: " + relativePath);
        }
        return resolved;
    }

    private String sanitize(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "dataset.csv";
        }
        // Keep only the file name, drop any directory components.
        String name = Paths.get(originalFileName).getFileName().toString();
        return name.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    /**
     * A stored file's location and size.
     *
     * @param relativePath forward-slashed path relative to the storage root
     * @param sizeBytes    number of bytes written
     */
    public record StoredFile(String relativePath, long sizeBytes) {
    }
}
