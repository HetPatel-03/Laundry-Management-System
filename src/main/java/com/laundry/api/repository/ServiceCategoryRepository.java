package com.laundry.api.repository;

import com.laundry.api.model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    
    Optional<ServiceCategory> findByName(String name);
    
    List<ServiceCategory> findByActive(boolean active);
    
    boolean existsByName(String name);
}