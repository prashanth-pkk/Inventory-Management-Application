package com.ims.service;

import com.ims.entity.User;
import com.ims.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //create a user
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    //find user by username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //find user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    //update user details
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }
}
