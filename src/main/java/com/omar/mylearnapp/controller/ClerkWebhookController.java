package com.omar.mylearnapp.controller;

import com.omar.mylearnapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/clerk")
public class ClerkWebhookController {


    @Autowired
    private UserService userService;

    @PostMapping("/user-created")
    public ResponseEntity<String> handleUserCreated(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            // Extracting user data
            String clerkId = (String) data.get("id");
            String email = ((List<Map<String, Object>>) data.get("email_addresses")).get(0).get("email_address").toString();
            String firstName = (String) data.get("first_name");
            String lastName = (String) data.get("last_name");

            // Extracting role from unsafe_metadata
            Map<String, Object> metadata = (Map<String, Object>) data.get("unsafe_metadata");
            String role = metadata != null ? (String) metadata.get("role") : "student"; // default to "student"

            // Save to database
            userService.createUser(clerkId, email, role, firstName, lastName);

            return ResponseEntity.ok("User successfully created");
        } catch (Exception e) {
            // Log the error and return a failure response
            System.err.println("Error processing Clerk webhook: " + e.getMessage());
            return ResponseEntity.status(500).body("Error processing the webhook");
        }
    }


}
