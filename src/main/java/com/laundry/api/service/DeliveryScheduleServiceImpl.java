package com.laundry.api.service;

import com.laundry.api.model.DeliverySchedule;
import com.laundry.api.model.Laundry;
import com.laundry.api.model.StaffActivity;
import com.laundry.api.model.User;
import com.laundry.api.repository.DeliveryScheduleRepository;
import com.laundry.api.repository.LaundryRepository;
import com.laundry.api.repository.StaffActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryScheduleServiceImpl implements DeliveryScheduleService {

    private final DeliveryScheduleRepository deliveryScheduleRepository;
    private final LaundryRepository laundryRepository;
    private final StaffActivityRepository staffActivityRepository;

    @Autowired
    public DeliveryScheduleServiceImpl(DeliveryScheduleRepository deliveryScheduleRepository,
                                      LaundryRepository laundryRepository,
                                      StaffActivityRepository staffActivityRepository) {
        this.deliveryScheduleRepository = deliveryScheduleRepository;
        this.laundryRepository = laundryRepository;
        this.staffActivityRepository = staffActivityRepository;
    }

    @Override
    public List<DeliverySchedule> getAllSchedules() {
        return deliveryScheduleRepository.findAll();
    }

    @Override
    public DeliverySchedule getScheduleById(Long id) {
        return deliveryScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Delivery schedule not found with id: " + id));
    }

    @Override
    public DeliverySchedule createSchedule(DeliverySchedule deliverySchedule) {
        // Validate that the laundry order exists
        Laundry laundry = laundryRepository.findById(deliverySchedule.getLaundry().getId())
                .orElseThrow(() -> new EntityNotFoundException("Laundry not found"));
                
        // Set the laundry order
        deliverySchedule.setLaundry(laundry);
        
        // If this is a pickup, update the laundry's scheduled pickup time
        if (deliverySchedule.getType() == DeliverySchedule.ScheduleType.PICKUP) {
            laundry.setScheduledPickupTime(deliverySchedule.getScheduledTime());
            laundry.setPickupAddress(deliverySchedule.getAddress());
            laundryRepository.save(laundry);
        } 
        // If this is a delivery, update the laundry's scheduled delivery time
        else if (deliverySchedule.getType() == DeliverySchedule.ScheduleType.DELIVERY) {
            laundry.setScheduledDeliveryTime(deliverySchedule.getScheduledTime());
            laundry.setDeliveryAddress(deliverySchedule.getAddress());
            laundryRepository.save(laundry);
        }
        
        // Set contact information if not provided
        if (deliverySchedule.getContactName() == null) {
            deliverySchedule.setContactName(laundry.getCustomer().getName());
        }
        
        if (deliverySchedule.getContactPhone() == null) {
            deliverySchedule.setContactPhone(laundry.getCustomer().getPhoneNumber());
        }
        
        return deliveryScheduleRepository.save(deliverySchedule);
    }

    @Override
    public DeliverySchedule updateSchedule(Long id, DeliverySchedule deliveryScheduleDetails) {
        DeliverySchedule deliverySchedule = getScheduleById(id);
        
        // Update allowed fields
        if (deliveryScheduleDetails.getScheduledTime() != null) {
            deliverySchedule.setScheduledTime(deliveryScheduleDetails.getScheduledTime());
            
            // Update the corresponding laundry scheduled time
            Laundry laundry = deliverySchedule.getLaundry();
            if (deliverySchedule.getType() == DeliverySchedule.ScheduleType.PICKUP) {
                laundry.setScheduledPickupTime(deliveryScheduleDetails.getScheduledTime());
                laundryRepository.save(laundry);
            } else if (deliverySchedule.getType() == DeliverySchedule.ScheduleType.DELIVERY) {
                laundry.setScheduledDeliveryTime(deliveryScheduleDetails.getScheduledTime());
                laundryRepository.save(laundry);
            }
        }
        
        if (deliveryScheduleDetails.getAddress() != null) {
            deliverySchedule.setAddress(deliveryScheduleDetails.getAddress());
            
            // Update the corresponding laundry address
            Laundry laundry = deliverySchedule.getLaundry();
            if (deliverySchedule.getType() == DeliverySchedule.ScheduleType.PICKUP) {
                laundry.setPickupAddress(deliveryScheduleDetails.getAddress());
                laundryRepository.save(laundry);
            } else if (deliverySchedule.getType() == DeliverySchedule.ScheduleType.DELIVERY) {
                laundry.setDeliveryAddress(deliveryScheduleDetails.getAddress());
                laundryRepository.save(laundry);
            }
        }
        
        if (deliveryScheduleDetails.getContactName() != null) {
            deliverySchedule.setContactName(deliveryScheduleDetails.getContactName());
        }
        
        if (deliveryScheduleDetails.getContactPhone() != null) {
            deliverySchedule.setContactPhone(deliveryScheduleDetails.getContactPhone());
        }
        
        if (deliveryScheduleDetails.getNotes() != null) {
            deliverySchedule.setNotes(deliveryScheduleDetails.getNotes());
        }
        
        if (deliveryScheduleDetails.getStatus() != null) {
            deliverySchedule.setStatus(deliveryScheduleDetails.getStatus());
        }
        
        return deliveryScheduleRepository.save(deliverySchedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        getScheduleById(id); // Check if exists
        deliveryScheduleRepository.deleteById(id);
    }

    @Override
    public List<DeliverySchedule> getSchedulesByStaff(User staff) {
        return deliveryScheduleRepository.findByStaff(staff);
    }

    @Override
    public List<DeliverySchedule> getSchedulesByStaffAndStatus(User staff, DeliverySchedule.ScheduleStatus status) {
        return deliveryScheduleRepository.findByStaffAndStatus(staff, status);
    }

    @Override
    public List<DeliverySchedule> getSchedulesByLaundryId(Long laundryId) {
        return deliveryScheduleRepository.findByLaundryId(laundryId);
    }

    @Override
    public List<DeliverySchedule> getSchedulesByType(DeliverySchedule.ScheduleType type) {
        return deliveryScheduleRepository.findByType(type);
    }

    @Override
    public List<DeliverySchedule> getSchedulesByStaffAndDateRange(Long staffId, LocalDateTime start, LocalDateTime end) {
        return deliveryScheduleRepository.findByStaffAndDateRange(staffId, start, end);
    }

    @Override
    public List<DeliverySchedule> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end) {
        return deliveryScheduleRepository.findByDateRange(start, end);
    }

    @Override
    public DeliverySchedule updateScheduleStatus(Long id, DeliverySchedule.ScheduleStatus status) {
        DeliverySchedule deliverySchedule = getScheduleById(id);
        deliverySchedule.setStatus(status);
        
        // Record staff activity
        StaffActivity activity = new StaffActivity();
        activity.setStaff(deliverySchedule.getStaff());
        activity.setLaundry(deliverySchedule.getLaundry());
        activity.setActivityType(StaffActivity.ActivityType.ORDER_STATUS_UPDATED);
        activity.setDescription("Updated " + deliverySchedule.getType() + " schedule status to " + status);
        staffActivityRepository.save(activity);
        
        return deliveryScheduleRepository.save(deliverySchedule);
    }

    @Override
    public DeliverySchedule completeSchedule(Long id) {
        DeliverySchedule deliverySchedule = getScheduleById(id);
        deliverySchedule.setStatus(DeliverySchedule.ScheduleStatus.COMPLETED);
        deliverySchedule.setCompletedTime(LocalDateTime.now());
        
        // Update the laundry order status and timestamps
        Laundry laundry = deliverySchedule.getLaundry();
        if (deliverySchedule.getType() == DeliverySchedule.ScheduleType.PICKUP) {
            laundry.setActualPickupTime(deliverySchedule.getCompletedTime());
            laundry.setStatus(Laundry.LaundryStatus.PICKED_UP);
            
            // Record staff activity
            StaffActivity activity = new StaffActivity();
            activity.setStaff(deliverySchedule.getStaff());
            activity.setLaundry(laundry);
            activity.setActivityType(StaffActivity.ActivityType.PICKUP_COMPLETED);
            activity.setDescription("Completed pickup for laundry order #" + laundry.getId());
            staffActivityRepository.save(activity);
        } 
        else if (deliverySchedule.getType() == DeliverySchedule.ScheduleType.DELIVERY) {
            laundry.setActualDeliveryTime(deliverySchedule.getCompletedTime());
            laundry.setStatus(Laundry.LaundryStatus.DELIVERED);
            laundry.setCompletedAt(deliverySchedule.getCompletedTime());
            
            // Record staff activity
            StaffActivity activity = new StaffActivity();
            activity.setStaff(deliverySchedule.getStaff());
            activity.setLaundry(laundry);
            activity.setActivityType(StaffActivity.ActivityType.DELIVERY_COMPLETED);
            activity.setDescription("Completed delivery for laundry order #" + laundry.getId());
            staffActivityRepository.save(activity);
        }
        
        laundryRepository.save(laundry);
        
        return deliveryScheduleRepository.save(deliverySchedule);
    }

    @Override
    public DeliverySchedule cancelSchedule(Long id) {
        DeliverySchedule deliverySchedule = getScheduleById(id);
        deliverySchedule.setStatus(DeliverySchedule.ScheduleStatus.CANCELLED);
        
        // Record staff activity
        StaffActivity activity = new StaffActivity();
        activity.setStaff(deliverySchedule.getStaff());
        activity.setLaundry(deliverySchedule.getLaundry());
        activity.setActivityType(StaffActivity.ActivityType.ORDER_STATUS_UPDATED);
        activity.setDescription("Cancelled " + deliverySchedule.getType() + " schedule");
        staffActivityRepository.save(activity);
        
        return deliveryScheduleRepository.save(deliverySchedule);
    }

    @Override
    public DeliverySchedule rescheduleDelivery(Long id, LocalDateTime newScheduledTime) {
        DeliverySchedule deliverySchedule = getScheduleById(id);
        deliverySchedule.setScheduledTime(newScheduledTime);
        deliverySchedule.setStatus(DeliverySchedule.ScheduleStatus.RESCHEDULED);
        
        // Update the laundry order scheduled times
        Laundry laundry = deliverySchedule.getLaundry();
        if (deliverySchedule.getType() == DeliverySchedule.ScheduleType.PICKUP) {
            laundry.setScheduledPickupTime(newScheduledTime);
        } else if (deliverySchedule.getType() == DeliverySchedule.ScheduleType.DELIVERY) {
            laundry.setScheduledDeliveryTime(newScheduledTime);
        }
        
        laundryRepository.save(laundry);
        
        // Record staff activity
        StaffActivity activity = new StaffActivity();
        activity.setStaff(deliverySchedule.getStaff());
        activity.setLaundry(deliverySchedule.getLaundry());
        activity.setActivityType(StaffActivity.ActivityType.ORDER_STATUS_UPDATED);
        activity.setDescription("Rescheduled " + deliverySchedule.getType() + " to " + newScheduledTime);
        staffActivityRepository.save(activity);
        
        return deliveryScheduleRepository.save(deliverySchedule);
    }
}