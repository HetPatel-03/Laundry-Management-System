package com.laundry.api.controller;

import com.laundry.api.dto.DeliveryScheduleDTO;
import com.laundry.api.model.ApiResponse;
import com.laundry.api.model.DeliverySchedule;
import com.laundry.api.model.Laundry;
import com.laundry.api.model.User;
import com.laundry.api.service.DeliveryScheduleService;
import com.laundry.api.service.LaundryService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@Tag(name = "Delivery Schedule Management", description = "APIs for managing pickup and delivery schedules")
@CrossOrigin(origins = "*")
public class DeliveryScheduleController {

    private final DeliveryScheduleService deliveryScheduleService;
    private final UserService userService;
    private final LaundryService laundryService;

    @Autowired
    public DeliveryScheduleController(DeliveryScheduleService deliveryScheduleService,
                                     UserService userService,
                                     LaundryService laundryService) {
        this.deliveryScheduleService = deliveryScheduleService;
        this.userService = userService;
        this.laundryService = laundryService;
    }

    @GetMapping
    @Operation(summary = "Get all schedules", description = "Retrieves a list of all delivery schedules")
    public ResponseEntity<ApiResponse<List<DeliveryScheduleDTO>>> getAllSchedules() {
        List<DeliverySchedule> schedules = deliveryScheduleService.getAllSchedules();
        List<DeliveryScheduleDTO> scheduleDTOs = schedules.stream()
                .map(DeliveryScheduleDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Delivery schedules retrieved successfully", scheduleDTOs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get schedule by ID", description = "Retrieves a delivery schedule by its ID")
    public ResponseEntity<ApiResponse<DeliveryScheduleDTO>> getScheduleById(@PathVariable Long id) {
        try {
            DeliverySchedule schedule = deliveryScheduleService.getScheduleById(id);
            return ResponseEntity.ok(ApiResponse.success("Delivery schedule retrieved successfully", 
                    DeliveryScheduleDTO.fromEntity(schedule)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create a new schedule", description = "Creates a new delivery schedule with the provided information")
    public ResponseEntity<ApiResponse<DeliveryScheduleDTO>> createSchedule(@Valid @RequestBody DeliveryScheduleDTO scheduleDTO) {
        try {
            // Get the staff from the database
            User staff = userService.getUserById(scheduleDTO.getStaffId());
            
            // Validate that the user is a staff
            if (staff.getRole() != User.Role.STAFF) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("The user is not a staff member"));
            }
            
            // Get the laundry order from the database
            Laundry laundry = laundryService.getLaundryById(scheduleDTO.getLaundryId());
            
            // Convert DTO to entity
            DeliverySchedule schedule = scheduleDTO.toEntity(staff, laundry);
            
            // Create the delivery schedule
            DeliverySchedule createdSchedule = deliveryScheduleService.createSchedule(schedule);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Delivery schedule created successfully", 
                            DeliveryScheduleDTO.fromEntity(createdSchedule)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a schedule", description = "Updates an existing delivery schedule with the provided information")
    public ResponseEntity<ApiResponse<DeliveryScheduleDTO>> updateSchedule(
            @PathVariable Long id, 
            @Valid @RequestBody DeliveryScheduleDTO scheduleDTO) {
        try {
            // Get the existing schedule
            DeliverySchedule existingSchedule = deliveryScheduleService.getScheduleById(id);
            
            // Create a new schedule object with the updated fields
            DeliverySchedule scheduleDetails = new DeliverySchedule();
            
            if (scheduleDTO.getScheduledTime() != null) {
                scheduleDetails.setScheduledTime(scheduleDTO.getScheduledTime());
            }
            
            if (scheduleDTO.getAddress() != null) {
                scheduleDetails.setAddress(scheduleDTO.getAddress());
            }
            
            if (scheduleDTO.getContactName() != null) {
                scheduleDetails.setContactName(scheduleDTO.getContactName());
            }
            
            if (scheduleDTO.getContactPhone() != null) {
                scheduleDetails.setContactPhone(scheduleDTO.getContactPhone());
            }
            
            if (scheduleDTO.getNotes() != null) {
                scheduleDetails.setNotes(scheduleDTO.getNotes());
            }
            
            if (scheduleDTO.getStatus() != null) {
                scheduleDetails.setStatus(scheduleDTO.getStatus());
            }
            
            // Update the delivery schedule
            DeliverySchedule updatedSchedule = deliveryScheduleService.updateSchedule(id, scheduleDetails);
            
            return ResponseEntity.ok(ApiResponse.success("Delivery schedule updated successfully", 
                    DeliveryScheduleDTO.fromEntity(updatedSchedule)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a schedule", description = "Deletes an existing delivery schedule by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long id) {
        try {
            deliveryScheduleService.deleteSchedule(id);
            return ResponseEntity.ok(ApiResponse.success("Delivery schedule deleted successfully", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/staff/{staffId}")
    @Operation(summary = "Get schedules by staff", description = "Retrieves all delivery schedules assigned to a specific staff")
    public ResponseEntity<ApiResponse<List<DeliveryScheduleDTO>>> getSchedulesByStaff(@PathVariable Long staffId) {
        try {
            User staff = userService.getUserById(staffId);
            List<DeliverySchedule> schedules = deliveryScheduleService.getSchedulesByStaff(staff);
            List<DeliveryScheduleDTO> scheduleDTOs = schedules.stream()
                    .map(DeliveryScheduleDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Delivery schedules retrieved successfully", scheduleDTOs));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/staff/{staffId}/status/{status}")
    @Operation(summary = "Get schedules by staff and status", description = "Retrieves all delivery schedules for a specific staff with a specific status")
    public ResponseEntity<ApiResponse<List<DeliveryScheduleDTO>>> getSchedulesByStaffAndStatus(
            @PathVariable Long staffId, 
            @PathVariable DeliverySchedule.ScheduleStatus status) {
        try {
            User staff = userService.getUserById(staffId);
            List<DeliverySchedule> schedules = deliveryScheduleService.getSchedulesByStaffAndStatus(staff, status);
            List<DeliveryScheduleDTO> scheduleDTOs = schedules.stream()
                    .map(DeliveryScheduleDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Delivery schedules retrieved successfully", scheduleDTOs));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/laundry/{laundryId}")
    @Operation(summary = "Get schedules by laundry ID", description = "Retrieves all delivery schedules for a specific laundry order")
    public ResponseEntity<ApiResponse<List<DeliveryScheduleDTO>>> getSchedulesByLaundryId(@PathVariable Long laundryId) {
        List<DeliverySchedule> schedules = deliveryScheduleService.getSchedulesByLaundryId(laundryId);
        List<DeliveryScheduleDTO> scheduleDTOs = schedules.stream()
                .map(DeliveryScheduleDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Delivery schedules retrieved successfully", scheduleDTOs));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get schedules by type", description = "Retrieves all delivery schedules of a specific type (pickup or delivery)")
    public ResponseEntity<ApiResponse<List<DeliveryScheduleDTO>>> getSchedulesByType(
            @PathVariable DeliverySchedule.ScheduleType type) {
        List<DeliverySchedule> schedules = deliveryScheduleService.getSchedulesByType(type);
        List<DeliveryScheduleDTO> scheduleDTOs = schedules.stream()
                .map(DeliveryScheduleDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Delivery schedules retrieved successfully", scheduleDTOs));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get schedules by date range", description = "Retrieves all delivery schedules within a specific date range")
    public ResponseEntity<ApiResponse<List<DeliveryScheduleDTO>>> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<DeliverySchedule> schedules = deliveryScheduleService.getSchedulesByDateRange(start, end);
        List<DeliveryScheduleDTO> scheduleDTOs = schedules.stream()
                .map(DeliveryScheduleDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Delivery schedules retrieved successfully", scheduleDTOs));
    }

    @GetMapping("/staff/{staffId}/date-range")
    @Operation(summary = "Get schedules by staff and date range", description = "Retrieves all delivery schedules for a specific staff within a date range")
    public ResponseEntity<ApiResponse<List<DeliveryScheduleDTO>>> getSchedulesByStaffAndDateRange(
            @PathVariable Long staffId, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<DeliverySchedule> schedules = deliveryScheduleService.getSchedulesByStaffAndDateRange(staffId, start, end);
        List<DeliveryScheduleDTO> scheduleDTOs = schedules.stream()
                .map(DeliveryScheduleDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Delivery schedules retrieved successfully", scheduleDTOs));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update schedule status", description = "Updates the status of an existing delivery schedule")
    public ResponseEntity<ApiResponse<DeliveryScheduleDTO>> updateScheduleStatus(
            @PathVariable Long id, 
            @RequestParam DeliverySchedule.ScheduleStatus status) {
        try {
            DeliverySchedule updatedSchedule = deliveryScheduleService.updateScheduleStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Delivery schedule status updated successfully", 
                    DeliveryScheduleDTO.fromEntity(updatedSchedule)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Complete a schedule", description = "Marks a delivery schedule as completed")
    public ResponseEntity<ApiResponse<DeliveryScheduleDTO>> completeSchedule(@PathVariable Long id) {
        try {
            DeliverySchedule completedSchedule = deliveryScheduleService.completeSchedule(id);
            return ResponseEntity.ok(ApiResponse.success("Delivery schedule completed successfully", 
                    DeliveryScheduleDTO.fromEntity(completedSchedule)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a schedule", description = "Cancels an existing delivery schedule")
    public ResponseEntity<ApiResponse<DeliveryScheduleDTO>> cancelSchedule(@PathVariable Long id) {
        try {
            DeliverySchedule cancelledSchedule = deliveryScheduleService.cancelSchedule(id);
            return ResponseEntity.ok(ApiResponse.success("Delivery schedule cancelled successfully", 
                    DeliveryScheduleDTO.fromEntity(cancelledSchedule)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule a delivery", description = "Reschedules an existing delivery schedule")
    public ResponseEntity<ApiResponse<DeliveryScheduleDTO>> rescheduleDelivery(
            @PathVariable Long id, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newScheduledTime) {
        try {
            DeliverySchedule rescheduledDelivery = deliveryScheduleService.rescheduleDelivery(id, newScheduledTime);
            return ResponseEntity.ok(ApiResponse.success("Delivery schedule rescheduled successfully", 
                    DeliveryScheduleDTO.fromEntity(rescheduledDelivery)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}