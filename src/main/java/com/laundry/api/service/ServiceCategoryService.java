package com.laundry.api.service;

import com.laundry.api.model.ServiceCategory;

import java.util.List;

public interface ServiceCategoryService {
    
    List<ServiceCategory> getAllServiceCategories();
    
    List<ServiceCategory> getActiveServiceCategories();
    
    ServiceCategory getServiceCategoryById(Long id);
    
    ServiceCategory getServiceCategoryByName(String name);
    
    ServiceCategory createServiceCategory(ServiceCategory serviceCategory);
    
    ServiceCategory updateServiceCategory(Long id, ServiceCategory serviceCategoryDetails);
    
    void deleteServiceCategory(Long id);
    
    ServiceCategory activateServiceCategory(Long id);
    
    ServiceCategory deactivateServiceCategory(Long id);
    
    boolean isServiceCategoryExists(String name);
}