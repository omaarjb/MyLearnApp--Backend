package com.omar.mylearnapp.model;

public class RoleUpdateRequest {
    private String clerkId;
    private String role;

    // Default constructor
    public RoleUpdateRequest() {}

    // Getters and setters
    public String getClerkId() {
        return clerkId;
    }

    public void setClerkId(String clerkId) {
        this.clerkId = clerkId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
