package com.laundry.api.service;

import com.laundry.api.model.User;
import com.laundry.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public User createUser(User user) {
        if (isEmailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        if (isPhoneNumberExists(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists: " + user.getPhoneNumber());
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(userDetails.getEmail()) && isEmailExists(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDetails.getEmail());
        }
        
        // Check if phone number is being changed and if it already exists
        if (!user.getPhoneNumber().equals(userDetails.getPhoneNumber()) && 
            isPhoneNumberExists(userDetails.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists: " + userDetails.getPhoneNumber());
        }
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setAddress(userDetails.getAddress());
        
        // Password should only be updated when provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }
        
        // Role can only be changed by admin, but for simplicity we'll allow it here
        user.setRole(userDetails.getRole());
        
        if (userDetails.getProfilePicture() != null) {
            user.setProfilePicture(userDetails.getProfilePicture());
        }
        
        // Don't allow activation status to be changed through general update
        
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        getUserById(id); // Check if exists
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> getActiveUsersByRole(User.Role role) {
        return userRepository.findByRoleAndActive(role, true);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("User not found with phone number: " + phoneNumber));
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneNumberExists(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public User activateUser(Long id) {
        User user = getUserById(id);
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public User deactivateUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        return userRepository.save(user);
    }
}