package com.laundry.api.service;

import com.laundry.api.model.User;

import java.util.List;

public interface UserService {
    
    List<User> getAllUsers();
    
    User getUserById(Long id);
    
    User createUser(User user);
    
    User updateUser(Long id, User userDetails);
    
    void deleteUser(Long id);
    
    List<User> getUsersByRole(User.Role role);
    
    List<User> getActiveUsersByRole(User.Role role);
    
    User getUserByEmail(String email);
    
    User getUserByPhoneNumber(String phoneNumber);
    
    boolean isEmailExists(String email);
    
    boolean isPhoneNumberExists(String phoneNumber);
    
    User activateUser(Long id);
    
    User deactivateUser(Long id);
}