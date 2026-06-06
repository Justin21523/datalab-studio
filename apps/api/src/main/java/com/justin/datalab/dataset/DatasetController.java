package com.justin.datalab.dataset;

import com.justin.datalab.common.exception.BadRequestException;
import com.justin.datalab.common.pagination.PageResponse;
import com.justin.datalab.common.response.ApiResponse;
import com.justin.datalab.shared.constant.ApiPaths;
import com.justin.datalab.shared.dto.DatasetDto;
import com.justin.datalab.shared.dto.DatasetPreviewDto;
import com.justin.datalab.shared.dto.DatasetProfileDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * HTTP endpoints for dataset import and inspection. Delegates all logic to
 * {@link DatasetService}.
 */
@RestController
@RequestMapping(ApiPaths.DATASETS)
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @PostMapping("/import/csv")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DatasetDto> importCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name) {
        if (file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }
        try {
            DatasetDto dto = datasetService.importCsv(
                    file.getInputStream(), file.getOriginalFilename(), name);
            return ApiResponse.ok(dto);
        } catch (IOException e) {
            throw new BadRequestException("Could not read uploaded file", e);
        }
    }

    @GetMapping
    public ApiResponse<PageResponse<DatasetDto>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(datasetService.list(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<DatasetDto> get(@PathVariable Long id) {
        return ApiResponse.ok(datasetService.get(id));
    }

    @GetMapping("/{id}" + ApiPaths.DATASET_PREVIEW)
    public ApiResponse<DatasetPreviewDto> preview(
            @PathVariable Long id,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return ApiResponse.ok(datasetService.preview(id, limit));
    }

    @GetMapping("/{id}" + ApiPaths.DATASET_PROFILE)
    public ApiResponse<DatasetProfileDto> profile(@PathVariable Long id) {
        return ApiResponse.ok(datasetService.profile(id));
    }
}
