package com.omar.mylearnapp.controller;

import com.omar.mylearnapp.model.RoleUpdateRequest;
import com.omar.mylearnapp.model.User;
import com.omar.mylearnapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/update-role")
    public ResponseEntity<String> updateRole(@RequestBody RoleUpdateRequest request) {
        boolean updated = userService.updateUserRole(request.getClerkId(), request.getRole());
        if (updated) {
            return ResponseEntity.ok("Role updated successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @GetMapping("/check-role")
    public ResponseEntity<?> checkUserRole(@RequestParam String clerkId) {
        Optional<User> user = userService.findByClerkId(clerkId);
        if (user.isPresent()) {
            boolean isProfessor = "professeur".equalsIgnoreCase(user.get().getRole());
            boolean isStudent = "student".equalsIgnoreCase(user.get().getRole());

            Map<String, Object> response = Map.of(
                    "isProfessor", isProfessor,
                    "isStudent", isStudent,
                    "role", user.get().getRole()
            );

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
    }

    @GetMapping("/by-clerk-id/{clerkId}")
    public ResponseEntity<?> getUserByClerkId(@PathVariable String clerkId) {
        Optional<User> user = userService.findByClerkId(clerkId);
        if (user.isPresent()) {
            User userData = user.get();
            // Create a response without sensitive information
            Map<String, Object> response = Map.of(
                    "id", userData.getId(),
                    "firstName", userData.getFirstName(),
                    "lastName", userData.getLastName(),
                    "email", userData.getEmail(),
                    "role", userData.getRole()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            User userData = user.get();
            // Create a response without sensitive information
            Map<String, Object> response = Map.of(
                    "id", userData.getId(),
                    "firstName", userData.getFirstName(),
                    "lastName", userData.getLastName(),
                    "email", userData.getEmail(),
                    "role", userData.getRole()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
    }
}
