package com.omar.mylearnapp.controller;

import com.omar.mylearnapp.model.RoleUpdateRequest;
import com.omar.mylearnapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
