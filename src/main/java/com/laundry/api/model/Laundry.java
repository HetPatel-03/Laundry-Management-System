package com.laundry.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "laundries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Laundry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Customer relationship
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Staff assignment (optional)
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User assignedStaff;

    // Service Category
    @ManyToOne
    @JoinColumn(name = "service_category_id", nullable = false)
    private ServiceCategory serviceCategory;

    @NotNull(message = "Number of items is required")
    @Column(nullable = false)
    private Integer numberOfItems;

    @NotNull(message = "Weight is required")
    @Column(nullable = false)
    private Double weight;

    @NotNull(message = "Price is required")
    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LaundryStatus status = LaundryStatus.PENDING;
    
    private String specialInstructions;

    // Pickup & Delivery information
    private LocalDateTime scheduledPickupTime;
    private LocalDateTime actualPickupTime;
    private String pickupAddress;
    
    private LocalDateTime scheduledDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private String deliveryAddress;
    
    // Order tracking timestamps
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    
    // Payment information
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    private String paymentReference;
    private LocalDateTime paidAt;

    // For supporting customer feedback
    private Integer rating;
    private String review;
    private LocalDateTime reviewedAt;

    public enum LaundryStatus {
        PENDING, CONFIRMED, PICKED_UP, PROCESSING, CLEANED, FOLDED, PACKAGED, 
        READY_FOR_DELIVERY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    }
    
    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED
    }
    
    public enum PaymentMethod {
        CASH, CREDIT_CARD, DEBIT_CARD, ONLINE, WALLET
    }
    
    // Helper methods
    public boolean isAssigned() {
        return assignedStaff != null;
    }
    
    public boolean isCompleted() {
        return status == LaundryStatus.DELIVERED;
    }
    
    public boolean isCancelled() {
        return status == LaundryStatus.CANCELLED;
    }
    
    public boolean isPaid() {
        return paymentStatus == PaymentStatus.PAID;
    }
    
    public boolean hasReview() {
        return rating != null && rating > 0;
    }
}