package com.tcet.pms.model;

import java.util.HashMap;
import java.util.Map;

/**
 * User Entity representing a team member or project manager.
 * Demonstrates Inheritance (extends BaseEntity) & Encapsulation.
 */
public class User extends BaseEntity {
    private String name;
    private String email;
    private String role; // "Developer", "Project Manager", "QA Engineer", etc.
    private String department;

    public User() {
        super();
        this.role = "Developer";
        this.department = "IT";
    }

    public User(String name, String email, String role, String department) {
        super();
        this.name = name;
        this.email = email;
        this.role = role != null ? role : "Developer";
        this.department = department != null ? department : "IT";
    }

    public User(String id, String name, String email, String role, String department, long createdAt, long updatedAt) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.email = email;
        this.role = role != null ? role : "Developer";
        this.department = department != null ? department : "IT";
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        touch();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        touch();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        touch();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
        touch();
    }

    /**
     * Map representation for clean JSON serialization.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", getId());
        map.put("name", name != null ? name : "");
        map.put("email", email != null ? email : "");
        map.put("role", role != null ? role : "Developer");
        map.put("department", department != null ? department : "IT");
        map.put("createdAt", getCreatedAt());
        map.put("updatedAt", getUpdatedAt());
        return map;
    }

    public static User fromMap(Map<String, Object> map) {
        String id = (String) map.get("id");
        String name = (String) map.get("name");
        String email = (String) map.get("email");
        String role = (String) map.get("role");
        String department = (String) map.get("department");
        long createdAt = map.containsKey("createdAt") ? ((Number) map.get("createdAt")).longValue() : 0L;
        long updatedAt = map.containsKey("updatedAt") ? ((Number) map.get("updatedAt")).longValue() : 0L;
        return new User(id, name, email, role, department, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return String.format("User[id=%s, name=%s, role=%s, email=%s]", getId(), name, role, email);
    }
}
