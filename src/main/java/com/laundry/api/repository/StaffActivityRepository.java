package com.laundry.api.repository;

import com.laundry.api.model.StaffActivity;
import com.laundry.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StaffActivityRepository extends JpaRepository<StaffActivity, Long> {
    
    List<StaffActivity> findByStaff(User staff);
    
    List<StaffActivity> findByLaundryId(Long laundryId);
    
    List<StaffActivity> findByActivityType(StaffActivity.ActivityType activityType);
    
    @Query("SELECT sa FROM StaffActivity sa WHERE sa.staff.id = :staffId AND sa.timestamp BETWEEN :start AND :end")
    List<StaffActivity> findByStaffAndDateRange(Long staffId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT sa FROM StaffActivity sa WHERE sa.timestamp BETWEEN :start AND :end")
    List<StaffActivity> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(sa) FROM StaffActivity sa WHERE sa.staff.id = :staffId AND sa.activityType = :activityType AND sa.timestamp BETWEEN :start AND :end")
    Long countActivitiesByStaffTypeAndDateRange(Long staffId, StaffActivity.ActivityType activityType, LocalDateTime start, LocalDateTime end);
}