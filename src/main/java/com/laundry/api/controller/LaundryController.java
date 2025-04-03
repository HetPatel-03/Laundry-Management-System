package com.laundry.api.controller;

import com.laundry.api.dto.LaundryDTO;
import com.laundry.api.model.ApiResponse;
import com.laundry.api.model.Laundry;
import com.laundry.api.model.ServiceCategory;
import com.laundry.api.model.User;
import com.laundry.api.service.LaundryService;
import com.laundry.api.service.ServiceCategoryService;
import com.laundry.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/laundry")
@Tag(name = "Laundry Management", description = "APIs for managing laundry orders")
@CrossOrigin(origins = "*")
public class LaundryController {

    private final LaundryService laundryService;
    private final UserService userService;
    private final ServiceCategoryService serviceCategoryService;

    @Autowired
    public LaundryController(LaundryService laundryService, 
                            UserService userService,
                            ServiceCategoryService serviceCategoryService) {
        this.laundryService = laundryService;
        this.userService = userService;
        this.serviceCategoryService = serviceCategoryService;
    }

    @GetMapping
    @Operation(summary = "Get all laundry orders", description = "Retrieves a list of all laundry orders")
    public ResponseEntity<ApiResponse<List<LaundryDTO>>> getAllLaundries() {
        List<Laundry> laundries = laundryService.getAllLaundries();
        List<LaundryDTO> laundryDTOs = laundries.stream()
                .map(LaundryDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Laundry orders retrieved successfully", laundryDTOs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get laundry order by ID", description = "Retrieves a laundry order by its ID")
    public ResponseEntity<ApiResponse<LaundryDTO>> getLaundryById(@PathVariable Long id) {
        try {
            Laundry laundry = laundryService.getLaundryById(id);
            return ResponseEntity.ok(ApiResponse.success("Laundry order retrieved successfully", 
                    LaundryDTO.fromEntity(laundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create a new laundry order", description = "Creates a new laundry order with the provided information")
    public ResponseEntity<ApiResponse<LaundryDTO>> createLaundry(@Valid @RequestBody LaundryDTO laundryDTO) {
        try {
            // Get the customer from the database
            User customer = userService.getUserById(laundryDTO.getCustomerId());
            
            // Get the service category from the database
            ServiceCategory serviceCategory = serviceCategoryService.getServiceCategoryById(laundryDTO.getServiceCategoryId());
            
            // Staff is optional and may be assigned later
            User staff = null;
            if (laundryDTO.getStaffId() != null) {
                staff = userService.getUserById(laundryDTO.getStaffId());
            }
            
            // Convert DTO to entity
            Laundry laundry = laundryDTO.toEntity(customer, staff, serviceCategory);
            
            // Create the laundry order
            Laundry createdLaundry = laundryService.createLaundry(laundry);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Laundry order created successfully", 
                            LaundryDTO.fromEntity(createdLaundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a laundry order", description = "Updates an existing laundry order with the provided information")
    public ResponseEntity<ApiResponse<LaundryDTO>> updateLaundry(
            @PathVariable Long id, 
            @Valid @RequestBody LaundryDTO laundryDTO) {
        try {
            // Get the existing laundry order
            Laundry existingLaundry = laundryService.getLaundryById(id);
            
            // Create a new laundry object with the updated fields
            Laundry laundryDetails = new Laundry();
            laundryDetails.setNumberOfItems(laundryDTO.getNumberOfItems());
            laundryDetails.setWeight(laundryDTO.getWeight());
            laundryDetails.setSpecialInstructions(laundryDTO.getSpecialInstructions());
            laundryDetails.setScheduledPickupTime(laundryDTO.getScheduledPickupTime());
            laundryDetails.setPickupAddress(laundryDTO.getPickupAddress());
            laundryDetails.setScheduledDeliveryTime(laundryDTO.getScheduledDeliveryTime());
            laundryDetails.setDeliveryAddress(laundryDTO.getDeliveryAddress());
            
            // Service category might be updated
            if (laundryDTO.getServiceCategoryId() != null && 
                (existingLaundry.getServiceCategory() == null || 
                !existingLaundry.getServiceCategory().getId().equals(laundryDTO.getServiceCategoryId()))) {
                
                ServiceCategory newServiceCategory = serviceCategoryService.getServiceCategoryById(laundryDTO.getServiceCategoryId());
                laundryDetails.setServiceCategory(newServiceCategory);
            }
            
            // Update the laundry order
            Laundry updatedLaundry = laundryService.updateLaundry(id, laundryDetails);
            
            return ResponseEntity.ok(ApiResponse.success("Laundry order updated successfully", 
                    LaundryDTO.fromEntity(updatedLaundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a laundry order", description = "Deletes an existing laundry order by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteLaundry(@PathVariable Long id) {
        try {
            laundryService.deleteLaundry(id);
            return ResponseEntity.ok(ApiResponse.success("Laundry order deleted successfully", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get laundries by customer", description = "Retrieves all laundry orders for a specific customer")
    public ResponseEntity<ApiResponse<List<LaundryDTO>>> getLaundriesByCustomer(@PathVariable Long customerId) {
        try {
            User customer = userService.getUserById(customerId);
            List<Laundry> laundries = laundryService.getLaundriesByCustomer(customer);
            List<LaundryDTO> laundryDTOs = laundries.stream()
                    .map(LaundryDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Laundry orders retrieved successfully", laundryDTOs));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/staff/{staffId}")
    @Operation(summary = "Get laundries by staff", description = "Retrieves all laundry orders assigned to a specific staff")
    public ResponseEntity<ApiResponse<List<LaundryDTO>>> getLaundriesByStaff(@PathVariable Long staffId) {
        try {
            User staff = userService.getUserById(staffId);
            List<Laundry> laundries = laundryService.getLaundriesByStaff(staff);
            List<LaundryDTO> laundryDTOs = laundries.stream()
                    .map(LaundryDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Laundry orders retrieved successfully", laundryDTOs));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get laundries by status", description = "Retrieves all laundry orders with a specific status")
    public ResponseEntity<ApiResponse<List<LaundryDTO>>> getLaundriesByStatus(@PathVariable Laundry.LaundryStatus status) {
        List<Laundry> laundries = laundryService.getLaundriesByStatus(status);
        List<LaundryDTO> laundryDTOs = laundries.stream()
                .map(LaundryDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Laundry orders retrieved successfully", laundryDTOs));
    }

    @GetMapping("/service/{serviceCategoryId}")
    @Operation(summary = "Get laundries by service category", description = "Retrieves all laundry orders with a specific service category")
    public ResponseEntity<ApiResponse<List<LaundryDTO>>> getLaundriesByServiceCategory(@PathVariable Long serviceCategoryId) {
        try {
            ServiceCategory serviceCategory = serviceCategoryService.getServiceCategoryById(serviceCategoryId);
            List<Laundry> laundries = laundryService.getLaundriesByServiceCategory(serviceCategory);
            List<LaundryDTO> laundryDTOs = laundries.stream()
                    .map(LaundryDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Laundry orders retrieved successfully", laundryDTOs));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update laundry status", description = "Updates the status of an existing laundry order")
    public ResponseEntity<ApiResponse<LaundryDTO>> updateLaundryStatus(
            @PathVariable Long id, 
            @RequestParam Laundry.LaundryStatus status) {
        try {
            Laundry updatedLaundry = laundryService.updateLaundryStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Laundry status updated successfully", 
                    LaundryDTO.fromEntity(updatedLaundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/assign-staff/{staffId}")
    @Operation(summary = "Assign staff to laundry", description = "Assigns a staff member to an existing laundry order")
    public ResponseEntity<ApiResponse<LaundryDTO>> assignStaffToLaundry(
            @PathVariable Long id, 
            @PathVariable Long staffId) {
        try {
            Laundry updatedLaundry = laundryService.assignStaff(id, staffId);
            return ResponseEntity.ok(ApiResponse.success("Staff assigned successfully", 
                    LaundryDTO.fromEntity(updatedLaundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/schedule-pickup")
    @Operation(summary = "Schedule pickup", description = "Schedules a pickup for an existing laundry order")
    public ResponseEntity<ApiResponse<LaundryDTO>> schedulePickup(
            @PathVariable Long id, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupTime, 
            @RequestParam String pickupAddress) {
        try {
            Laundry updatedLaundry = laundryService.schedulePickup(id, pickupTime, pickupAddress);
            return ResponseEntity.ok(ApiResponse.success("Pickup scheduled successfully", 
                    LaundryDTO.fromEntity(updatedLaundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/schedule-delivery")
    @Operation(summary = "Schedule delivery", description = "Schedules a delivery for an existing laundry order")
    public ResponseEntity<ApiResponse<LaundryDTO>> scheduleDelivery(
            @PathVariable Long id, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deliveryTime, 
            @RequestParam String deliveryAddress) {
        try {
            Laundry updatedLaundry = laundryService.scheduleDelivery(id, deliveryTime, deliveryAddress);
            return ResponseEntity.ok(ApiResponse.success("Delivery scheduled successfully", 
                    LaundryDTO.fromEntity(updatedLaundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/record-pickup")
    @Operation(summary = "Record pickup", description = "Records a pickup for an existing laundry order")
    public ResponseEntity<ApiResponse<LaundryDTO>> recordPickup(
            @PathVariable Long id, 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime actualPickupTime) {
        try {
            if (actualPickupTime == null) {
                actualPickupTime = LocalDateTime.now();
            }
            Laundry updatedLaundry = laundryService.recordPickup(id, actualPickupTime);
            return ResponseEntity.ok(ApiResponse.success("Pickup recorded successfully", 
                    LaundryDTO.fromEntity(updatedLaundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/record-delivery")
    @Operation(summary = "Record delivery", description = "Records a delivery for an existing laundry order")
    public ResponseEntity<ApiResponse<LaundryDTO>> recordDelivery(
            @PathVariable Long id, 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime actualDeliveryTime) {
        try {
            if (actualDeliveryTime == null) {
                actualDeliveryTime = LocalDateTime.now();
            }
            Laundry updatedLaundry = laundryService.recordDelivery(id, actualDeliveryTime);
            return ResponseEntity.ok(ApiResponse.success("Delivery recorded successfully", 
                    LaundryDTO.fromEntity(updatedLaundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/review")
    @Operation(summary = "Submit review", description = "Submits a review for a completed laundry order")
    public ResponseEntity<ApiResponse<LaundryDTO>> submitReview(
            @PathVariable Long id, 
            @RequestParam Integer rating, 
            @RequestParam(required = false) String review) {
        try {
            Laundry updatedLaundry = laundryService.submitReview(id, rating, review);
            return ResponseEntity.ok(ApiResponse.success("Review submitted successfully", 
                    LaundryDTO.fromEntity(updatedLaundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/payment")
    @Operation(summary = "Record payment", description = "Records a payment for a laundry order")
    public ResponseEntity<ApiResponse<LaundryDTO>> recordPayment(
            @PathVariable Long id, 
            @RequestParam Laundry.PaymentMethod paymentMethod, 
            @RequestParam(required = false) String paymentReference) {
        try {
            Laundry updatedLaundry = laundryService.recordPayment(id, paymentMethod, paymentReference);
            return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", 
                    LaundryDTO.fromEntity(updatedLaundry)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics", description = "Retrieves statistics for the dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStatistics() {
        Map<String, Object> statistics = laundryService.getDashboardStatistics();
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved successfully", statistics));
    }

    @GetMapping("/reports/staff-productivity")
    @Operation(summary = "Get staff productivity report", description = "Retrieves staff productivity report for a date range")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStaffProductivityReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Map<String, Object> report = laundryService.getStaffProductivityReport(start, end);
        return ResponseEntity.ok(ApiResponse.success("Staff productivity report retrieved successfully", report));
    }

    @GetMapping("/reports/revenue")
    @Operation(summary = "Get revenue report", description = "Retrieves revenue report for a date range")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Map<String, Object> report = laundryService.getRevenueReport(start, end);
        return ResponseEntity.ok(ApiResponse.success("Revenue report retrieved successfully", report));
    }

    @GetMapping("/reports/customer/{customerId}")
    @Operation(summary = "Get customer order report", description = "Retrieves order report for a specific customer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerOrderReport(@PathVariable Long customerId) {
        try {
            Map<String, Object> report = laundryService.getCustomerOrderReport(customerId);
            return ResponseEntity.ok(ApiResponse.success("Customer order report retrieved successfully", report));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/reports/service-category")
    @Operation(summary = "Get service category report", description = "Retrieves service category report for a date range")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getServiceCategoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Map<String, Object> report = laundryService.getServiceCategoryReport(start, end);
        return ResponseEntity.ok(ApiResponse.success("Service category report retrieved successfully", report));
    }
}