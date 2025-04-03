package com.laundry.api.dto;

import com.laundry.api.model.Laundry;
import com.laundry.api.model.ServiceCategory;
import com.laundry.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaundryDTO {
    
    private Long id;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    private String customerName; // For display purposes, populated from the customer
    
    private Long staffId; // Optional - may be assigned later
    private String staffName; // For display purposes, populated from the staff
    
    @NotNull(message = "Service category ID is required")
    private Long serviceCategoryId;
    private String serviceCategoryName; // For display purposes, populated from the service category
    
    @NotNull(message = "Number of items is required")
    private Integer numberOfItems;
    
    @NotNull(message = "Weight is required")
    private Double weight;
    
    private BigDecimal price; // Calculated based on service and weight
    private BigDecimal taxAmount; // Calculated
    private BigDecimal totalAmount; // Calculated
    
    private Laundry.LaundryStatus status;
    
    private String specialInstructions;
    
    // Pickup & Delivery information
    private LocalDateTime scheduledPickupTime;
    private LocalDateTime actualPickupTime;
    private String pickupAddress;
    
    private LocalDateTime scheduledDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private String deliveryAddress;
    
    // Payment information
    private Laundry.PaymentStatus paymentStatus;
    private Laundry.PaymentMethod paymentMethod;
    private String paymentReference;
    private LocalDateTime paidAt;
    
    // Review information
    private Integer rating;
    private String review;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime reviewedAt;
    
    // Convert from Entity to DTO
    public static LaundryDTO fromEntity(Laundry laundry) {
        LaundryDTO dto = new LaundryDTO();
        dto.setId(laundry.getId());
        
        // Customer information
        if (laundry.getCustomer() != null) {
            dto.setCustomerId(laundry.getCustomer().getId());
            dto.setCustomerName(laundry.getCustomer().getName());
        }
        
        // Staff information
        if (laundry.getAssignedStaff() != null) {
            dto.setStaffId(laundry.getAssignedStaff().getId());
            dto.setStaffName(laundry.getAssignedStaff().getName());
        }
        
        // Service category information
        if (laundry.getServiceCategory() != null) {
            dto.setServiceCategoryId(laundry.getServiceCategory().getId());
            dto.setServiceCategoryName(laundry.getServiceCategory().getName());
        }
        
        dto.setNumberOfItems(laundry.getNumberOfItems());
        dto.setWeight(laundry.getWeight());
        dto.setPrice(laundry.getPrice());
        dto.setTaxAmount(laundry.getTaxAmount());
        dto.setTotalAmount(laundry.getTotalAmount());
        dto.setStatus(laundry.getStatus());
        dto.setSpecialInstructions(laundry.getSpecialInstructions());
        
        // Pickup & Delivery information
        dto.setScheduledPickupTime(laundry.getScheduledPickupTime());
        dto.setActualPickupTime(laundry.getActualPickupTime());
        dto.setPickupAddress(laundry.getPickupAddress());
        dto.setScheduledDeliveryTime(laundry.getScheduledDeliveryTime());
        dto.setActualDeliveryTime(laundry.getActualDeliveryTime());
        dto.setDeliveryAddress(laundry.getDeliveryAddress());
        
        // Payment information
        dto.setPaymentStatus(laundry.getPaymentStatus());
        dto.setPaymentMethod(laundry.getPaymentMethod());
        dto.setPaymentReference(laundry.getPaymentReference());
        dto.setPaidAt(laundry.getPaidAt());
        
        // Review information
        dto.setRating(laundry.getRating());
        dto.setReview(laundry.getReview());
        dto.setReviewedAt(laundry.getReviewedAt());
        
        // Timestamps
        dto.setCreatedAt(laundry.getCreatedAt());
        dto.setUpdatedAt(laundry.getUpdatedAt());
        dto.setCompletedAt(laundry.getCompletedAt());
        dto.setCancelledAt(laundry.getCancelledAt());
        
        return dto;
    }
    
    // Convert from DTO to Entity
    public Laundry toEntity(User customer, User staff, ServiceCategory serviceCategory) {
        Laundry laundry = new Laundry();
        laundry.setId(this.id); // Will be null for new laundry orders
        
        // Set related entities
        laundry.setCustomer(customer);
        laundry.setAssignedStaff(staff);
        laundry.setServiceCategory(serviceCategory);
        
        laundry.setNumberOfItems(this.numberOfItems);
        laundry.setWeight(this.weight);
        laundry.setSpecialInstructions(this.specialInstructions);
        
        // Pickup & Delivery information
        laundry.setScheduledPickupTime(this.scheduledPickupTime);
        laundry.setPickupAddress(this.pickupAddress);
        laundry.setScheduledDeliveryTime(this.scheduledDeliveryTime);
        laundry.setDeliveryAddress(this.deliveryAddress);
        
        // Status (may be set to default in service)
        if (this.status != null) {
            laundry.setStatus(this.status);
        }
        
        return laundry;
    }
}