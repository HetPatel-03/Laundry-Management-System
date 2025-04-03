package com.laundry.api.dto;

import com.laundry.api.model.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategoryDTO {
    
    private Long id;
    
    @NotBlank(message = "Service name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;
    
    @NotNull(message = "Price per kg is required")
    @Positive(message = "Price per kg must be positive")
    private BigDecimal pricePerKg;
    
    private Integer estimatedTimeInHours;
    
    private boolean active = true;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Convert from Entity to DTO
    public static ServiceCategoryDTO fromEntity(ServiceCategory serviceCategory) {
        ServiceCategoryDTO dto = new ServiceCategoryDTO();
        dto.setId(serviceCategory.getId());
        dto.setName(serviceCategory.getName());
        dto.setDescription(serviceCategory.getDescription());
        dto.setBasePrice(serviceCategory.getBasePrice());
        dto.setPricePerKg(serviceCategory.getPricePerKg());
        dto.setEstimatedTimeInHours(serviceCategory.getEstimatedTimeInHours());
        dto.setActive(serviceCategory.isActive());
        dto.setCreatedAt(serviceCategory.getCreatedAt());
        dto.setUpdatedAt(serviceCategory.getUpdatedAt());
        return dto;
    }
    
    // Convert from DTO to Entity
    public ServiceCategory toEntity() {
        ServiceCategory serviceCategory = new ServiceCategory();
        serviceCategory.setId(this.id); // Will be null for new service categories
        serviceCategory.setName(this.name);
        serviceCategory.setDescription(this.description);
        serviceCategory.setBasePrice(this.basePrice);
        serviceCategory.setPricePerKg(this.pricePerKg);
        serviceCategory.setEstimatedTimeInHours(this.estimatedTimeInHours);
        serviceCategory.setActive(this.active);
        return serviceCategory;
    }
}