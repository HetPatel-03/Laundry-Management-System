package com.laundry.api.dto;

import com.laundry.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private User.Role role;
    
    private String address;
    
    private String profilePicture;
    
    private boolean active = true;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Convert from Entity to DTO
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        // Don't set password in DTO for security
        dto.setRole(user.getRole());
        dto.setAddress(user.getAddress());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
    
    // Convert from DTO to Entity
    public User toEntity() {
        User user = new User();
        user.setId(this.id); // Will be null for new users
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPhoneNumber(this.phoneNumber);
        user.setPassword(this.password);
        user.setRole(this.role);
        user.setAddress(this.address);
        user.setProfilePicture(this.profilePicture);
        user.setActive(this.active);
        return user;
    }
}