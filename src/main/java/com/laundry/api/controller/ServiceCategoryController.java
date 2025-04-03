package com.laundry.api.controller;

import com.laundry.api.dto.ServiceCategoryDTO;
import com.laundry.api.model.ApiResponse;
import com.laundry.api.model.ServiceCategory;
import com.laundry.api.service.ServiceCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Service Category Management", description = "APIs for managing laundry service categories")
@CrossOrigin(origins = "*")
public class ServiceCategoryController {

    private final ServiceCategoryService serviceCategoryService;

    @Autowired
    public ServiceCategoryController(ServiceCategoryService serviceCategoryService) {
        this.serviceCategoryService = serviceCategoryService;
    }

    @GetMapping
    @Operation(summary = "Get all service categories", description = "Retrieves a list of all service categories")
    public ResponseEntity<ApiResponse<List<ServiceCategoryDTO>>> getAllServiceCategories() {
        List<ServiceCategory> categories = serviceCategoryService.getAllServiceCategories();
        List<ServiceCategoryDTO> categoryDTOs = categories.stream()
                .map(ServiceCategoryDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Service categories retrieved successfully", categoryDTOs));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active service categories", description = "Retrieves a list of active service categories")
    public ResponseEntity<ApiResponse<List<ServiceCategoryDTO>>> getActiveServiceCategories() {
        List<ServiceCategory> categories = serviceCategoryService.getActiveServiceCategories();
        List<ServiceCategoryDTO> categoryDTOs = categories.stream()
                .map(ServiceCategoryDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Active service categories retrieved successfully", categoryDTOs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service category by ID", description = "Retrieves a service category by its ID")
    public ResponseEntity<ApiResponse<ServiceCategoryDTO>> getServiceCategoryById(@PathVariable Long id) {
        try {
            ServiceCategory category = serviceCategoryService.getServiceCategoryById(id);
            return ResponseEntity.ok(ApiResponse.success("Service category retrieved successfully", 
                    ServiceCategoryDTO.fromEntity(category)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create a new service category", description = "Creates a new service category with the provided information")
    public ResponseEntity<ApiResponse<ServiceCategoryDTO>> createServiceCategory(@Valid @RequestBody ServiceCategoryDTO categoryDTO) {
        try {
            ServiceCategory category = categoryDTO.toEntity();
            ServiceCategory createdCategory = serviceCategoryService.createServiceCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Service category created successfully", 
                            ServiceCategoryDTO.fromEntity(createdCategory)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a service category", description = "Updates an existing service category with the provided information")
    public ResponseEntity<ApiResponse<ServiceCategoryDTO>> updateServiceCategory(
            @PathVariable Long id, 
            @Valid @RequestBody ServiceCategoryDTO categoryDTO) {
        try {
            ServiceCategory categoryDetails = categoryDTO.toEntity();
            ServiceCategory updatedCategory = serviceCategoryService.updateServiceCategory(id, categoryDetails);
            return ResponseEntity.ok(ApiResponse.success("Service category updated successfully", 
                    ServiceCategoryDTO.fromEntity(updatedCategory)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a service category", description = "Deletes an existing service category by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteServiceCategory(@PathVariable Long id) {
        try {
            serviceCategoryService.deleteServiceCategory(id);
            return ResponseEntity.ok(ApiResponse.success("Service category deleted successfully", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a service category", description = "Activates a service category")
    public ResponseEntity<ApiResponse<ServiceCategoryDTO>> activateServiceCategory(@PathVariable Long id) {
        try {
            ServiceCategory category = serviceCategoryService.activateServiceCategory(id);
            return ResponseEntity.ok(ApiResponse.success("Service category activated successfully", 
                    ServiceCategoryDTO.fromEntity(category)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a service category", description = "Deactivates a service category")
    public ResponseEntity<ApiResponse<ServiceCategoryDTO>> deactivateServiceCategory(@PathVariable Long id) {
        try {
            ServiceCategory category = serviceCategoryService.deactivateServiceCategory(id);
            return ResponseEntity.ok(ApiResponse.success("Service category deactivated successfully", 
                    ServiceCategoryDTO.fromEntity(category)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}