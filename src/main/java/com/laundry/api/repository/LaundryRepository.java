package com.laundry.api.repository;

import com.laundry.api.model.Laundry;
import com.laundry.api.model.ServiceCategory;
import com.laundry.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LaundryRepository extends JpaRepository<Laundry, Long> {
    
    List<Laundry> findByCustomer(User customer);
    
    List<Laundry> findByAssignedStaff(User staff);
    
    List<Laundry> findByStatus(Laundry.LaundryStatus status);
    
    List<Laundry> findByServiceCategory(ServiceCategory serviceCategory);
    
    List<Laundry> findByAssignedStaffAndStatus(User staff, Laundry.LaundryStatus status);
    
    List<Laundry> findByCustomerAndStatus(User customer, Laundry.LaundryStatus status);
    
    List<Laundry> findByPaymentStatus(Laundry.PaymentStatus paymentStatus);
    
    List<Laundry> findByPaymentMethod(Laundry.PaymentMethod paymentMethod);
    
    @Query("SELECT l FROM Laundry l WHERE l.createdAt BETWEEN :start AND :end")
    List<Laundry> findByCreatedDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT l FROM Laundry l WHERE l.scheduledPickupTime BETWEEN :start AND :end")
    List<Laundry> findByPickupDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT l FROM Laundry l WHERE l.scheduledDeliveryTime BETWEEN :start AND :end")
    List<Laundry> findByDeliveryDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(l) FROM Laundry l WHERE l.status = :status")
    Long countByStatus(Laundry.LaundryStatus status);
    
    @Query("SELECT COUNT(l) FROM Laundry l WHERE l.assignedStaff.id = :staffId AND l.status = :status")
    Long countByStaffAndStatus(Long staffId, Laundry.LaundryStatus status);
    
    @Query("SELECT SUM(l.totalAmount) FROM Laundry l WHERE l.paymentStatus = 'PAID' AND l.paidAt BETWEEN :start AND :end")
    Double getTotalRevenueForPeriod(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT AVG(l.rating) FROM Laundry l WHERE l.rating IS NOT NULL")
    Double getAverageRating();
}