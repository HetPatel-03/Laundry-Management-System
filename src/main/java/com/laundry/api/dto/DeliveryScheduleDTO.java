package com.laundry.api.dto;

import com.laundry.api.model.DeliverySchedule;
import com.laundry.api.model.Laundry;
import com.laundry.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryScheduleDTO {
    
    private Long id;
    
    @NotNull(message = "Staff ID is required")
    private Long staffId;
    private String staffName; // For display purposes
    
    @NotNull(message = "Laundry ID is required")
    private Long laundryId;
    private String customerName; // For display purposes
    
    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledTime;
    
    private LocalDateTime completedTime;
    
    @NotNull(message = "Schedule type is required")
    private DeliverySchedule.ScheduleType type;
    
    private DeliverySchedule.ScheduleStatus status;
    
    private String notes;
    
    @NotNull(message = "Address is required")
    private String address;
    
    private String contactName;
    
    private String contactPhone;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Convert from Entity to DTO
    public static DeliveryScheduleDTO fromEntity(DeliverySchedule deliverySchedule) {
        DeliveryScheduleDTO dto = new DeliveryScheduleDTO();
        dto.setId(deliverySchedule.getId());
        
        // Staff information
        if (deliverySchedule.getStaff() != null) {
            dto.setStaffId(deliverySchedule.getStaff().getId());
            dto.setStaffName(deliverySchedule.getStaff().getName());
        }
        
        // Laundry information
        if (deliverySchedule.getLaundry() != null) {
            dto.setLaundryId(deliverySchedule.getLaundry().getId());
            if (deliverySchedule.getLaundry().getCustomer() != null) {
                dto.setCustomerName(deliverySchedule.getLaundry().getCustomer().getName());
            }
        }
        
        dto.setScheduledTime(deliverySchedule.getScheduledTime());
        dto.setCompletedTime(deliverySchedule.getCompletedTime());
        dto.setType(deliverySchedule.getType());
        dto.setStatus(deliverySchedule.getStatus());
        dto.setNotes(deliverySchedule.getNotes());
        dto.setAddress(deliverySchedule.getAddress());
        dto.setContactName(deliverySchedule.getContactName());
        dto.setContactPhone(deliverySchedule.getContactPhone());
        dto.setCreatedAt(deliverySchedule.getCreatedAt());
        dto.setUpdatedAt(deliverySchedule.getUpdatedAt());
        
        return dto;
    }
    
    // Convert from DTO to Entity
    public DeliverySchedule toEntity(User staff, Laundry laundry) {
        DeliverySchedule deliverySchedule = new DeliverySchedule();
        deliverySchedule.setId(this.id); // Will be null for new schedules
        
        // Set related entities
        deliverySchedule.setStaff(staff);
        deliverySchedule.setLaundry(laundry);
        
        deliverySchedule.setScheduledTime(this.scheduledTime);
        deliverySchedule.setType(this.type);
        
        // Set status if provided, otherwise use default (PENDING)
        if (this.status != null) {
            deliverySchedule.setStatus(this.status);
        }
        
        deliverySchedule.setNotes(this.notes);
        deliverySchedule.setAddress(this.address);
        deliverySchedule.setContactName(this.contactName);
        deliverySchedule.setContactPhone(this.contactPhone);
        
        return deliverySchedule;
    }
}