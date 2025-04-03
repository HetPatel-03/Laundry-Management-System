package com.laundry.api.service;

import com.laundry.api.model.Laundry;
import com.laundry.api.model.ServiceCategory;
import com.laundry.api.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface LaundryService {
    
    List<Laundry> getAllLaundries();
    
    Laundry getLaundryById(Long id);
    
    Laundry createLaundry(Laundry laundry);
    
    Laundry updateLaundry(Long id, Laundry laundryDetails);
    
    void deleteLaundry(Long id);
    
    List<Laundry> getLaundriesByCustomer(User customer);
    
    List<Laundry> getLaundriesByStaff(User staff);
    
    List<Laundry> getLaundriesByStatus(Laundry.LaundryStatus status);
    
    List<Laundry> getLaundriesByServiceCategory(ServiceCategory serviceCategory);
    
    List<Laundry> getLaundriesByStaffAndStatus(User staff, Laundry.LaundryStatus status);
    
    List<Laundry> getLaundriesByCustomerAndStatus(User customer, Laundry.LaundryStatus status);
    
    Laundry updateLaundryStatus(Long id, Laundry.LaundryStatus status);
    
    Laundry assignStaff(Long laundryId, Long staffId);
    
    Laundry schedulePickup(Long laundryId, LocalDateTime pickupTime, String pickupAddress);
    
    Laundry scheduleDelivery(Long laundryId, LocalDateTime deliveryTime, String deliveryAddress);
    
    Laundry recordPickup(Long laundryId, LocalDateTime actualPickupTime);
    
    Laundry recordDelivery(Long laundryId, LocalDateTime actualDeliveryTime);
    
    Laundry submitReview(Long laundryId, Integer rating, String review);
    
    Laundry recordPayment(Long laundryId, Laundry.PaymentMethod paymentMethod, String paymentReference);
    
    List<Laundry> getLaundriesByCreatedDateRange(LocalDateTime start, LocalDateTime end);
    
    List<Laundry> getLaundriesByPickupDateRange(LocalDateTime start, LocalDateTime end);
    
    List<Laundry> getLaundriesByDeliveryDateRange(LocalDateTime start, LocalDateTime end);
    
    Map<String, Long> getLaundryStatusCounts();
    
    Map<String, Long> getStaffLaundryStatusCounts(Long staffId);
    
    Double getTotalRevenueForPeriod(LocalDateTime start, LocalDateTime end);
    
    Double getAverageRating();
    
    // Additional analytics methods
    Map<String, Object> getDashboardStatistics();
    
    Map<String, Object> getStaffProductivityReport(LocalDateTime start, LocalDateTime end);
    
    Map<String, Object> getRevenueReport(LocalDateTime start, LocalDateTime end);
    
    Map<String, Object> getCustomerOrderReport(Long customerId);
    
    Map<String, Object> getServiceCategoryReport(LocalDateTime start, LocalDateTime end);
}