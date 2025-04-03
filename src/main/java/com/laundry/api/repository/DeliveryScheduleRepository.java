package com.laundry.api.repository;

import com.laundry.api.model.DeliverySchedule;
import com.laundry.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeliveryScheduleRepository extends JpaRepository<DeliverySchedule, Long> {
    
    List<DeliverySchedule> findByStaff(User staff);
    
    List<DeliverySchedule> findByStaffAndStatus(User staff, DeliverySchedule.ScheduleStatus status);
    
    List<DeliverySchedule> findByLaundryId(Long laundryId);
    
    List<DeliverySchedule> findByType(DeliverySchedule.ScheduleType type);
    
    @Query("SELECT ds FROM DeliverySchedule ds WHERE ds.staff.id = :staffId AND ds.scheduledTime BETWEEN :start AND :end")
    List<DeliverySchedule> findByStaffAndDateRange(Long staffId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT ds FROM DeliverySchedule ds WHERE ds.scheduledTime BETWEEN :start AND :end")
    List<DeliverySchedule> findByDateRange(LocalDateTime start, LocalDateTime end);
}