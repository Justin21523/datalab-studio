package com.justin.datalab.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.justin.datalab.shared.constant.ApiPaths;

/**
 * Web layer configuration. Enables {@link StorageProperties} binding and opens
 * CORS for the local desktop client (which calls the API from a different
 * origin during development).
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(ApiPaths.API_BASE + "/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
