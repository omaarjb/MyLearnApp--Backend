package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.User;
import com.omar.mylearnapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void createUser(String clerkId, String email, String role, String firstName, String lastName) {

        if (!userRepository.existsByClerkId(clerkId)) {
            User user = new User();
            user.setClerkId(clerkId);
            user.setEmail(email);
            user.setRole(role);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userRepository.save(user);
        }

    }

    public boolean updateUserRole(String clerkId, String role) {
        User user = userRepository.findByClerkId(clerkId).orElse(null);
        if (user != null) {
            user.setRole(role);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteUserByClerkId(String clerkId) {
        userRepository.deleteByClerkId(clerkId);
    }

    public Optional<User> findByClerkId(String clerkId) {
        return userRepository.findByClerkId(clerkId);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
