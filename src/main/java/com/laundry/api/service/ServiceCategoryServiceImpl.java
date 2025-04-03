package com.laundry.api.service;

import com.laundry.api.model.ServiceCategory;
import com.laundry.api.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    public ServiceCategoryServiceImpl(ServiceCategoryRepository serviceCategoryRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
    }

    @Override
    public List<ServiceCategory> getAllServiceCategories() {
        return serviceCategoryRepository.findAll();
    }

    @Override
    public List<ServiceCategory> getActiveServiceCategories() {
        return serviceCategoryRepository.findByActive(true);
    }

    @Override
    public ServiceCategory getServiceCategoryById(Long id) {
        return serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service category not found with id: " + id));
    }

    @Override
    public ServiceCategory getServiceCategoryByName(String name) {
        return serviceCategoryRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Service category not found with name: " + name));
    }

    @Override
    public ServiceCategory createServiceCategory(ServiceCategory serviceCategory) {
        if (isServiceCategoryExists(serviceCategory.getName())) {
            throw new IllegalArgumentException("Service category already exists with name: " + serviceCategory.getName());
        }
        return serviceCategoryRepository.save(serviceCategory);
    }

    @Override
    public ServiceCategory updateServiceCategory(Long id, ServiceCategory serviceCategoryDetails) {
        ServiceCategory serviceCategory = getServiceCategoryById(id);
        
        // Check if name is being changed and if it already exists
        if (!serviceCategory.getName().equals(serviceCategoryDetails.getName()) && 
            isServiceCategoryExists(serviceCategoryDetails.getName())) {
            throw new IllegalArgumentException("Service category already exists with name: " + serviceCategoryDetails.getName());
        }
        
        serviceCategory.setName(serviceCategoryDetails.getName());
        serviceCategory.setDescription(serviceCategoryDetails.getDescription());
        serviceCategory.setBasePrice(serviceCategoryDetails.getBasePrice());
        serviceCategory.setPricePerKg(serviceCategoryDetails.getPricePerKg());
        serviceCategory.setEstimatedTimeInHours(serviceCategoryDetails.getEstimatedTimeInHours());
        
        // Don't update active status through general update
        
        return serviceCategoryRepository.save(serviceCategory);
    }

    @Override
    public void deleteServiceCategory(Long id) {
        getServiceCategoryById(id); // Check if exists
        serviceCategoryRepository.deleteById(id);
    }

    @Override
    public ServiceCategory activateServiceCategory(Long id) {
        ServiceCategory serviceCategory = getServiceCategoryById(id);
        serviceCategory.setActive(true);
        return serviceCategoryRepository.save(serviceCategory);
    }

    @Override
    public ServiceCategory deactivateServiceCategory(Long id) {
        ServiceCategory serviceCategory = getServiceCategoryById(id);
        serviceCategory.setActive(false);
        return serviceCategoryRepository.save(serviceCategory);
    }

    @Override
    public boolean isServiceCategoryExists(String name) {
        return serviceCategoryRepository.existsByName(name);
    }
}