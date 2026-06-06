package com.justin.datalab.common;

import com.justin.datalab.common.response.ApiResponse;
import com.justin.datalab.shared.constant.ApiPaths;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Liveness endpoint used by the desktop client and operators to confirm the
 * service is up.
 */
@RestController
public class HealthController {

    @GetMapping(ApiPaths.HEALTH)
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.ok(Map.of(
                "status", "UP",
                "service", "datalab-api"
        ));
    }
}
