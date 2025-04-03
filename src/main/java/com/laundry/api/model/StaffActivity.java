package com.laundry.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    @ManyToOne
    @JoinColumn(name = "laundry_id")
    private Laundry laundry;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    private String description;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;

    public enum ActivityType {
        ORDER_ASSIGNED,
        ORDER_STATUS_UPDATED,
        PICKUP_COMPLETED,
        DELIVERY_COMPLETED,
        LAUNDRY_PROCESSED
    }
}