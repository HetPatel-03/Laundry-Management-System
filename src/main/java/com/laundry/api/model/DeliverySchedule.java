package com.laundry.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliverySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    @ManyToOne
    @JoinColumn(name = "laundry_id", nullable = false)
    private Laundry laundry;

    @NotNull
    private LocalDateTime scheduledTime;

    private LocalDateTime completedTime;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status = ScheduleStatus.PENDING;

    private String notes;

    private String address;
    
    private String contactName;
    
    private String contactPhone;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ScheduleType {
        PICKUP, DELIVERY
    }

    public enum ScheduleStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED, RESCHEDULED
    }
}