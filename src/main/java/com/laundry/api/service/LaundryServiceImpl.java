package com.laundry.api.service;

import com.laundry.api.model.*;
import com.laundry.api.repository.LaundryRepository;
import com.laundry.api.repository.ServiceCategoryRepository;
import com.laundry.api.repository.StaffActivityRepository;
import com.laundry.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LaundryServiceImpl implements LaundryService {

    private final LaundryRepository laundryRepository;
    private final UserRepository userRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final StaffActivityRepository staffActivityRepository;

    @Autowired
    public LaundryServiceImpl(LaundryRepository laundryRepository, 
                             UserRepository userRepository,
                             ServiceCategoryRepository serviceCategoryRepository,
                             StaffActivityRepository staffActivityRepository) {
        this.laundryRepository = laundryRepository;
        this.userRepository = userRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.staffActivityRepository = staffActivityRepository;
    }

    @Override
    public List<Laundry> getAllLaundries() {
        return laundryRepository.findAll();
    }

    @Override
    public Laundry getLaundryById(Long id) {
        return laundryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Laundry not found with id: " + id));
    }

    @Override
    public Laundry createLaundry(Laundry laundry) {
        // Validate customer existence
        User customer = userRepository.findById(laundry.getCustomer().getId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
                
        // Validate service category existence
        ServiceCategory serviceCategory = serviceCategoryRepository.findById(laundry.getServiceCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Service category not found"));
                
        // Set the customer and service category
        laundry.setCustomer(customer);
        laundry.setServiceCategory(serviceCategory);
        
        // Calculate the price based on service category and weight
        BigDecimal basePrice = serviceCategory.getBasePrice();
        BigDecimal pricePerKg = serviceCategory.getPricePerKg();
        BigDecimal weightPrice = pricePerKg.multiply(BigDecimal.valueOf(laundry.getWeight()));
        BigDecimal price = basePrice.add(weightPrice);
        laundry.setPrice(price);
        
        // Calculate tax (assuming 10% tax)
        BigDecimal taxAmount = price.multiply(BigDecimal.valueOf(0.1));
        laundry.setTaxAmount(taxAmount);
        
        // Calculate total amount
        BigDecimal totalAmount = price.add(taxAmount);
        laundry.setTotalAmount(totalAmount);
        
        // Set initial status
        laundry.setStatus(Laundry.LaundryStatus.PENDING);
        laundry.setPaymentStatus(Laundry.PaymentStatus.PENDING);
        
        return laundryRepository.save(laundry);
    }

    @Override
    public Laundry updateLaundry(Long id, Laundry laundryDetails) {
        Laundry laundry = getLaundryById(id);
        
        // Only allow updates to certain fields
        if (laundryDetails.getNumberOfItems() != null) {
            laundry.setNumberOfItems(laundryDetails.getNumberOfItems());
        }
        
        if (laundryDetails.getWeight() != null) {
            laundry.setWeight(laundryDetails.getWeight());
            
            // Recalculate price if weight changes
            ServiceCategory serviceCategory = laundry.getServiceCategory();
            BigDecimal basePrice = serviceCategory.getBasePrice();
            BigDecimal pricePerKg = serviceCategory.getPricePerKg();
            BigDecimal weightPrice = pricePerKg.multiply(BigDecimal.valueOf(laundry.getWeight()));
            BigDecimal price = basePrice.add(weightPrice);
            laundry.setPrice(price);
            
            // Recalculate tax and total
            BigDecimal taxAmount = price.multiply(BigDecimal.valueOf(0.1));
            laundry.setTaxAmount(taxAmount);
            laundry.setTotalAmount(price.add(taxAmount));
        }
        
        if (laundryDetails.getServiceCategory() != null && laundryDetails.getServiceCategory().getId() != null) {
            ServiceCategory newServiceCategory = serviceCategoryRepository.findById(laundryDetails.getServiceCategory().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Service category not found"));
            
            laundry.setServiceCategory(newServiceCategory);
            
            // Recalculate price if service category changes
            BigDecimal basePrice = newServiceCategory.getBasePrice();
            BigDecimal pricePerKg = newServiceCategory.getPricePerKg();
            BigDecimal weightPrice = pricePerKg.multiply(BigDecimal.valueOf(laundry.getWeight()));
            BigDecimal price = basePrice.add(weightPrice);
            laundry.setPrice(price);
            
            // Recalculate tax and total
            BigDecimal taxAmount = price.multiply(BigDecimal.valueOf(0.1));
            laundry.setTaxAmount(taxAmount);
            laundry.setTotalAmount(price.add(taxAmount));
        }
        
        if (laundryDetails.getSpecialInstructions() != null) {
            laundry.setSpecialInstructions(laundryDetails.getSpecialInstructions());
        }
        
        if (laundryDetails.getScheduledPickupTime() != null) {
            laundry.setScheduledPickupTime(laundryDetails.getScheduledPickupTime());
        }
        
        if (laundryDetails.getPickupAddress() != null) {
            laundry.setPickupAddress(laundryDetails.getPickupAddress());
        }
        
        if (laundryDetails.getScheduledDeliveryTime() != null) {
            laundry.setScheduledDeliveryTime(laundryDetails.getScheduledDeliveryTime());
        }
        
        if (laundryDetails.getDeliveryAddress() != null) {
            laundry.setDeliveryAddress(laundryDetails.getDeliveryAddress());
        }
        
        return laundryRepository.save(laundry);
    }

    @Override
    public void deleteLaundry(Long id) {
        getLaundryById(id); // Check if exists
        laundryRepository.deleteById(id);
    }

    @Override
    public List<Laundry> getLaundriesByCustomer(User customer) {
        return laundryRepository.findByCustomer(customer);
    }

    @Override
    public List<Laundry> getLaundriesByStaff(User staff) {
        return laundryRepository.findByAssignedStaff(staff);
    }

    @Override
    public List<Laundry> getLaundriesByStatus(Laundry.LaundryStatus status) {
        return laundryRepository.findByStatus(status);
    }

    @Override
    public List<Laundry> getLaundriesByServiceCategory(ServiceCategory serviceCategory) {
        return laundryRepository.findByServiceCategory(serviceCategory);
    }

    @Override
    public List<Laundry> getLaundriesByStaffAndStatus(User staff, Laundry.LaundryStatus status) {
        return laundryRepository.findByAssignedStaffAndStatus(staff, status);
    }

    @Override
    public List<Laundry> getLaundriesByCustomerAndStatus(User customer, Laundry.LaundryStatus status) {
        return laundryRepository.findByCustomerAndStatus(customer, status);
    }

    @Override
    public Laundry updateLaundryStatus(Long id, Laundry.LaundryStatus status) {
        Laundry laundry = getLaundryById(id);
        laundry.setStatus(status);
        
        // Set specific timestamps based on status
        if (status == Laundry.LaundryStatus.DELIVERED) {
            laundry.setCompletedAt(LocalDateTime.now());
        } else if (status == Laundry.LaundryStatus.CANCELLED) {
            laundry.setCancelledAt(LocalDateTime.now());
        }
        
        // Record staff activity if the laundry is assigned to a staff
        if (laundry.getAssignedStaff() != null) {
            StaffActivity activity = new StaffActivity();
            activity.setStaff(laundry.getAssignedStaff());
            activity.setLaundry(laundry);
            activity.setActivityType(StaffActivity.ActivityType.ORDER_STATUS_UPDATED);
            activity.setDescription("Updated laundry status to " + status);
            staffActivityRepository.save(activity);
        }
        
        return laundryRepository.save(laundry);
    }

    @Override
    public Laundry assignStaff(Long laundryId, Long staffId) {
        Laundry laundry = getLaundryById(laundryId);
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with id: " + staffId));
        
        // Verify the user is a staff
        if (staff.getRole() != User.Role.STAFF) {
            throw new IllegalArgumentException("The user is not a staff member");
        }
        
        laundry.setAssignedStaff(staff);
        
        // Record staff activity
        StaffActivity activity = new StaffActivity();
        activity.setStaff(staff);
        activity.setLaundry(laundry);
        activity.setActivityType(StaffActivity.ActivityType.ORDER_ASSIGNED);
        activity.setDescription("Assigned to laundry order #" + laundryId);
        staffActivityRepository.save(activity);
        
        return laundryRepository.save(laundry);
    }

    @Override
    public Laundry schedulePickup(Long laundryId, LocalDateTime pickupTime, String pickupAddress) {
        Laundry laundry = getLaundryById(laundryId);
        laundry.setScheduledPickupTime(pickupTime);
        laundry.setPickupAddress(pickupAddress);
        return laundryRepository.save(laundry);
    }

    @Override
    public Laundry scheduleDelivery(Long laundryId, LocalDateTime deliveryTime, String deliveryAddress) {
        Laundry laundry = getLaundryById(laundryId);
        laundry.setScheduledDeliveryTime(deliveryTime);
        laundry.setDeliveryAddress(deliveryAddress);
        return laundryRepository.save(laundry);
    }

    @Override
    public Laundry recordPickup(Long laundryId, LocalDateTime actualPickupTime) {
        Laundry laundry = getLaundryById(laundryId);
        laundry.setActualPickupTime(actualPickupTime);
        laundry.setStatus(Laundry.LaundryStatus.PICKED_UP);
        
        // Record staff activity if assigned
        if (laundry.getAssignedStaff() != null) {
            StaffActivity activity = new StaffActivity();
            activity.setStaff(laundry.getAssignedStaff());
            activity.setLaundry(laundry);
            activity.setActivityType(StaffActivity.ActivityType.PICKUP_COMPLETED);
            activity.setDescription("Completed pickup for laundry order #" + laundryId);
            staffActivityRepository.save(activity);
        }
        
        return laundryRepository.save(laundry);
    }

    @Override
    public Laundry recordDelivery(Long laundryId, LocalDateTime actualDeliveryTime) {
        Laundry laundry = getLaundryById(laundryId);
        laundry.setActualDeliveryTime(actualDeliveryTime);
        laundry.setStatus(Laundry.LaundryStatus.DELIVERED);
        laundry.setCompletedAt(actualDeliveryTime);
        
        // Record staff activity if assigned
        if (laundry.getAssignedStaff() != null) {
            StaffActivity activity = new StaffActivity();
            activity.setStaff(laundry.getAssignedStaff());
            activity.setLaundry(laundry);
            activity.setActivityType(StaffActivity.ActivityType.DELIVERY_COMPLETED);
            activity.setDescription("Completed delivery for laundry order #" + laundryId);
            staffActivityRepository.save(activity);
        }
        
        return laundryRepository.save(laundry);
    }

    @Override
    public Laundry submitReview(Long laundryId, Integer rating, String review) {
        Laundry laundry = getLaundryById(laundryId);
        
        // Only allow reviews for completed orders
        if (laundry.getStatus() != Laundry.LaundryStatus.DELIVERED) {
            throw new IllegalStateException("Cannot review an order that hasn't been delivered");
        }
        
        laundry.setRating(rating);
        laundry.setReview(review);
        laundry.setReviewedAt(LocalDateTime.now());
        
        return laundryRepository.save(laundry);
    }

    @Override
    public Laundry recordPayment(Long laundryId, Laundry.PaymentMethod paymentMethod, String paymentReference) {
        Laundry laundry = getLaundryById(laundryId);
        
        laundry.setPaymentMethod(paymentMethod);
        laundry.setPaymentReference(paymentReference);
        laundry.setPaymentStatus(Laundry.PaymentStatus.PAID);
        laundry.setPaidAt(LocalDateTime.now());
        
        return laundryRepository.save(laundry);
    }

    @Override
    public List<Laundry> getLaundriesByCreatedDateRange(LocalDateTime start, LocalDateTime end) {
        return laundryRepository.findByCreatedDateRange(start, end);
    }

    @Override
    public List<Laundry> getLaundriesByPickupDateRange(LocalDateTime start, LocalDateTime end) {
        return laundryRepository.findByPickupDateRange(start, end);
    }

    @Override
    public List<Laundry> getLaundriesByDeliveryDateRange(LocalDateTime start, LocalDateTime end) {
        return laundryRepository.findByDeliveryDateRange(start, end);
    }

    @Override
    public Map<String, Long> getLaundryStatusCounts() {
        Map<String, Long> statusCounts = new HashMap<>();
        
        for (Laundry.LaundryStatus status : Laundry.LaundryStatus.values()) {
            statusCounts.put(status.name(), laundryRepository.countByStatus(status));
        }
        
        return statusCounts;
    }

    @Override
    public Map<String, Long> getStaffLaundryStatusCounts(Long staffId) {
        Map<String, Long> statusCounts = new HashMap<>();
        
        for (Laundry.LaundryStatus status : Laundry.LaundryStatus.values()) {
            statusCounts.put(status.name(), laundryRepository.countByStaffAndStatus(staffId, status));
        }
        
        return statusCounts;
    }

    @Override
    public Double getTotalRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        return laundryRepository.getTotalRevenueForPeriod(start, end);
    }

    @Override
    public Double getAverageRating() {
        return laundryRepository.getAverageRating();
    }

    @Override
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Get total number of orders
        long totalOrders = laundryRepository.count();
        statistics.put("totalOrders", totalOrders);
        
        // Get pending orders
        long pendingOrders = laundryRepository.countByStatus(Laundry.LaundryStatus.PENDING);
        statistics.put("pendingOrders", pendingOrders);
        
        // Get in-progress orders
        long processingOrders = laundryRepository.countByStatus(Laundry.LaundryStatus.PROCESSING);
        statistics.put("processingOrders", processingOrders);
        
        // Get completed orders
        long completedOrders = laundryRepository.countByStatus(Laundry.LaundryStatus.DELIVERED);
        statistics.put("completedOrders", completedOrders);
        
        // Get orders by status
        statistics.put("ordersByStatus", getLaundryStatusCounts());
        
        // Get total revenue for current month
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        Double monthlyRevenue = laundryRepository.getTotalRevenueForPeriod(firstDayOfMonth, LocalDateTime.now());
        statistics.put("monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : 0.0);
        
        // Get average rating
        Double avgRating = laundryRepository.getAverageRating();
        statistics.put("averageRating", avgRating != null ? avgRating : 0.0);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getStaffProductivityReport(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> report = new HashMap<>();
        
        // Get all staff members
        List<User> staffMembers = userRepository.findByRole(User.Role.STAFF);
        
        List<Map<String, Object>> staffStats = new ArrayList<>();
        
        for (User staff : staffMembers) {
            Map<String, Object> staffStat = new HashMap<>();
            staffStat.put("staffId", staff.getId());
            staffStat.put("staffName", staff.getName());
            
            // Get count of orders assigned to this staff in the period
            List<Laundry> assignedOrders = laundryRepository.findByAssignedStaff(staff)
                    .stream()
                    .filter(l -> l.getCreatedAt().isAfter(start) && l.getCreatedAt().isBefore(end))
                    .collect(Collectors.toList());
            
            staffStat.put("assignedOrders", assignedOrders.size());
            
            // Count completed orders
            long completedOrders = assignedOrders.stream()
                    .filter(l -> l.getStatus() == Laundry.LaundryStatus.DELIVERED)
                    .count();
            staffStat.put("completedOrders", completedOrders);
            
            // Calculate completion rate
            double completionRate = assignedOrders.isEmpty() ? 0 : (double) completedOrders / assignedOrders.size();
            staffStat.put("completionRate", completionRate);
            
            // Get average processing time (in hours)
            OptionalDouble avgProcessingTime = assignedOrders.stream()
                    .filter(l -> l.getCompletedAt() != null)
                    .mapToDouble(l -> {
                        long hours = ChronoUnit.HOURS.between(l.getCreatedAt(), l.getCompletedAt());
                        return hours;
                    })
                    .average();
                    
            staffStat.put("averageProcessingTime", avgProcessingTime.orElse(0));
            
            // Get pickups and deliveries
            Long pickups = staffActivityRepository.countActivitiesByStaffTypeAndDateRange(
                    staff.getId(), StaffActivity.ActivityType.PICKUP_COMPLETED, start, end);
                    
            Long deliveries = staffActivityRepository.countActivitiesByStaffTypeAndDateRange(
                    staff.getId(), StaffActivity.ActivityType.DELIVERY_COMPLETED, start, end);
                    
            staffStat.put("pickups", pickups);
            staffStat.put("deliveries", deliveries);
            
            staffStats.add(staffStat);
        }
        
        report.put("staffStats", staffStats);
        report.put("startDate", start);
        report.put("endDate", end);
        
        return report;
    }

    @Override
    public Map<String, Object> getRevenueReport(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> report = new HashMap<>();
        
        // Get orders in the period
        List<Laundry> orders = laundryRepository.findByCreatedDateRange(start, end);
        
        // Calculate total revenue
        BigDecimal totalRevenue = orders.stream()
                .filter(l -> l.getPaymentStatus() == Laundry.PaymentStatus.PAID)
                .map(Laundry::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        report.put("totalRevenue", totalRevenue);
        
        // Calculate total tax
        BigDecimal totalTax = orders.stream()
                .filter(l -> l.getPaymentStatus() == Laundry.PaymentStatus.PAID)
                .map(Laundry::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        report.put("totalTax", totalTax);
        
        // Calculate revenue by service category
        Map<String, BigDecimal> revenueByService = new HashMap<>();
        
        for (Laundry order : orders) {
            if (order.getPaymentStatus() == Laundry.PaymentStatus.PAID) {
                String serviceName = order.getServiceCategory().getName();
                BigDecimal currentAmount = revenueByService.getOrDefault(serviceName, BigDecimal.ZERO);
                revenueByService.put(serviceName, currentAmount.add(order.getTotalAmount()));
            }
        }
        
        report.put("revenueByService", revenueByService);
        
        // Calculate revenue by payment method
        Map<String, BigDecimal> revenueByPaymentMethod = new HashMap<>();
        
        for (Laundry order : orders) {
            if (order.getPaymentStatus() == Laundry.PaymentStatus.PAID && order.getPaymentMethod() != null) {
                String paymentMethod = order.getPaymentMethod().name();
                BigDecimal currentAmount = revenueByPaymentMethod.getOrDefault(paymentMethod, BigDecimal.ZERO);
                revenueByPaymentMethod.put(paymentMethod, currentAmount.add(order.getTotalAmount()));
            }
        }
        
        report.put("revenueByPaymentMethod", revenueByPaymentMethod);
        
        // Get order count
        report.put("totalOrders", orders.size());
        
        // Get paid order count
        long paidOrders = orders.stream()
                .filter(l -> l.getPaymentStatus() == Laundry.PaymentStatus.PAID)
                .count();
                
        report.put("paidOrders", paidOrders);
        
        // Calculate average order value
        BigDecimal avgOrderValue = paidOrders > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(paidOrders), BigDecimal.ROUND_HALF_UP) : 
                BigDecimal.ZERO;
                
        report.put("averageOrderValue", avgOrderValue);
        
        report.put("startDate", start);
        report.put("endDate", end);
        
        return report;
    }

    @Override
    public Map<String, Object> getCustomerOrderReport(Long customerId) {
        Map<String, Object> report = new HashMap<>();
        
        // Get customer
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
                
        report.put("customerId", customer.getId());
        report.put("customerName", customer.getName());
        report.put("customerEmail", customer.getEmail());
        report.put("customerPhone", customer.getPhoneNumber());
        
        // Get all orders for this customer
        List<Laundry> orders = laundryRepository.findByCustomer(customer);
        
        report.put("totalOrders", orders.size());
        
        // Calculate total spent
        BigDecimal totalSpent = orders.stream()
                .filter(l -> l.getPaymentStatus() == Laundry.PaymentStatus.PAID)
                .map(Laundry::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        report.put("totalSpent", totalSpent);
        
        // Get favorite service (most used)
        Map<String, Long> serviceUsageCounts = orders.stream()
                .collect(Collectors.groupingBy(
                    o -> o.getServiceCategory().getName(),
                    Collectors.counting()
                ));
                
        Optional<Map.Entry<String, Long>> favoriteService = serviceUsageCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue());
                
        report.put("favoriteService", favoriteService.isPresent() ? favoriteService.get().getKey() : "None");
        
        // Calculate average rating given
        OptionalDouble avgRating = orders.stream()
                .filter(o -> o.getRating() != null)
                .mapToInt(Laundry::getRating)
                .average();
                
        report.put("averageRating", avgRating.orElse(0.0));
        
        // Get most recent orders
        List<Map<String, Object>> recentOrders = orders.stream()
                .sorted(Comparator.comparing(Laundry::getCreatedAt).reversed())
                .limit(5)
                .map(o -> {
                    Map<String, Object> orderInfo = new HashMap<>();
                    orderInfo.put("id", o.getId());
                    orderInfo.put("createdAt", o.getCreatedAt());
                    orderInfo.put("status", o.getStatus());
                    orderInfo.put("service", o.getServiceCategory().getName());
                    orderInfo.put("totalAmount", o.getTotalAmount());
                    return orderInfo;
                })
                .collect(Collectors.toList());
                
        report.put("recentOrders", recentOrders);
        
        return report;
    }

    @Override
    public Map<String, Object> getServiceCategoryReport(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> report = new HashMap<>();
        
        // Get all service categories
        List<ServiceCategory> categories = serviceCategoryRepository.findAll();
        
        List<Map<String, Object>> serviceStats = new ArrayList<>();
        
        for (ServiceCategory category : categories) {
            Map<String, Object> serviceStat = new HashMap<>();
            serviceStat.put("serviceId", category.getId());
            serviceStat.put("serviceName", category.getName());
            
            // Get orders for this service in the period
            List<Laundry> serviceOrders = laundryRepository.findByServiceCategory(category)
                    .stream()
                    .filter(l -> l.getCreatedAt().isAfter(start) && l.getCreatedAt().isBefore(end))
                    .collect(Collectors.toList());
                    
            serviceStat.put("orderCount", serviceOrders.size());
            
            // Calculate total revenue for this service
            BigDecimal totalRevenue = serviceOrders.stream()
                    .filter(l -> l.getPaymentStatus() == Laundry.PaymentStatus.PAID)
                    .map(Laundry::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
            serviceStat.put("totalRevenue", totalRevenue);
            
            // Calculate average order value
            BigDecimal avgOrderValue;
            long paidOrders = serviceOrders.stream()
                    .filter(l -> l.getPaymentStatus() == Laundry.PaymentStatus.PAID)
                    .count();
                    
            if (paidOrders > 0) {
                avgOrderValue = totalRevenue.divide(BigDecimal.valueOf(paidOrders), BigDecimal.ROUND_HALF_UP);
            } else {
                avgOrderValue = BigDecimal.ZERO;
            }
            
            serviceStat.put("averageOrderValue", avgOrderValue);
            
            // Calculate average rating
            OptionalDouble avgRating = serviceOrders.stream()
                    .filter(o -> o.getRating() != null)
                    .mapToInt(Laundry::getRating)
                    .average();
                    
            serviceStat.put("averageRating", avgRating.orElse(0.0));
            
            serviceStats.add(serviceStat);
        }
        
        report.put("serviceStats", serviceStats);
        report.put("startDate", start);
        report.put("endDate", end);
        
        return report;
    }
}