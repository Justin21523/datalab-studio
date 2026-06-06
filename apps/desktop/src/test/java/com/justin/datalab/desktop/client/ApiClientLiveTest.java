package com.justin.datalab.desktop.client;

import com.justin.datalab.shared.dto.DatasetDto;
import com.justin.datalab.shared.dto.DatasetPreviewDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Exercises {@link ApiClient} (and the desktop-side JSON deserialization of the
 * shared DTOs) against a live backend. Skipped unless {@code DATALAB_API_BASE_URL}
 * is set and the API responds, so it never fails an offline build.
 *
 * <p>To run it: start the backend, then
 * {@code DATALAB_API_BASE_URL=http://localhost:8099 ./mvnw -pl apps/desktop test -Dtest=ApiClientLiveTest}.</p>
 */
class ApiClientLiveTest {

    private static ApiClient client;

    @BeforeAll
    static void setUp() {
        String baseUrl = System.getenv("DATALAB_API_BASE_URL");
        assumeTrue(baseUrl != null && !baseUrl.isBlank(),
                "DATALAB_API_BASE_URL not set; skipping live test");
        client = new ApiClient(baseUrl);
        boolean up;
        try {
            up = "UP".equals(client.health());
        } catch (RuntimeException e) {
            up = false;
        }
        assumeTrue(up, "Backend not reachable; skipping live test");
    }

    @Test
    void listsDatasetsThenPreviewsAndProfilesTheFirst() {
        List<DatasetDto> datasets = client.listDatasets();
        assumeTrue(!datasets.isEmpty(), "No datasets to inspect");

        DatasetDto first = datasets.get(0);
        assertNotNull(first.name());
        assertNotNull(first.status());
        assertNotNull(first.createdAt(), "Instant should deserialize from ISO-8601");

        DatasetPreviewDto preview = client.preview(first.id(), 5);
        assertFalse(preview.columns().isEmpty(), "preview should have columns");

        DatasetProfileDto profile = client.profile(first.id());
        assertFalse(profile.columns().isEmpty(), "profile should have columns");
    }
}
