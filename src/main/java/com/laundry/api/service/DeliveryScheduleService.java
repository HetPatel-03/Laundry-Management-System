package com.laundry.api.service;

import com.laundry.api.model.DeliverySchedule;
import com.laundry.api.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryScheduleService {
    
    List<DeliverySchedule> getAllSchedules();
    
    DeliverySchedule getScheduleById(Long id);
    
    DeliverySchedule createSchedule(DeliverySchedule deliverySchedule);
    
    DeliverySchedule updateSchedule(Long id, DeliverySchedule deliveryScheduleDetails);
    
    void deleteSchedule(Long id);
    
    List<DeliverySchedule> getSchedulesByStaff(User staff);
    
    List<DeliverySchedule> getSchedulesByStaffAndStatus(User staff, DeliverySchedule.ScheduleStatus status);
    
    List<DeliverySchedule> getSchedulesByLaundryId(Long laundryId);
    
    List<DeliverySchedule> getSchedulesByType(DeliverySchedule.ScheduleType type);
    
    List<DeliverySchedule> getSchedulesByStaffAndDateRange(Long staffId, LocalDateTime start, LocalDateTime end);
    
    List<DeliverySchedule> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end);
    
    DeliverySchedule updateScheduleStatus(Long id, DeliverySchedule.ScheduleStatus status);
    
    DeliverySchedule completeSchedule(Long id);
    
    DeliverySchedule cancelSchedule(Long id);
    
    DeliverySchedule rescheduleDelivery(Long id, LocalDateTime newScheduledTime);
}