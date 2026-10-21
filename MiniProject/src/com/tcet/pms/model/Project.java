package com.tcet.pms.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Entity representing a software project container.
 * Inherits BaseEntity.
 */
public class Project extends BaseEntity {
    private String name;
    private String description;
    private Priority priority;
    private String status; // "Active", "On Hold", "Completed"
    private String owner;  // User ID or name

    public Project() {
        super();
        this.priority = Priority.MEDIUM;
        this.status = "Active";
    }

    public Project(String name, String description) {
        super();
        this.name = name;
        this.description = description;
        this.priority = Priority.MEDIUM;
        this.status = "Active";
    }

    public Project(String id, String name, String description, Priority priority, String status, String owner, long createdAt, long updatedAt) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.description = description;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.status = status != null ? status : "Active";
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        touch();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        touch();
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        touch();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        touch();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
        touch();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", getId());
        map.put("name", name != null ? name : "");
        map.put("description", description != null ? description : "");
        map.put("priority", priority != null ? priority.getCode() : "medium");
        map.put("status", status != null ? status : "Active");
        map.put("owner", owner != null ? owner : "");
        map.put("createdAt", getCreatedAt());
        map.put("updatedAt", getUpdatedAt());
        return map;
    }

    public static Project fromMap(Map<String, Object> map) {
        String id = (String) map.get("id");
        String name = (String) map.get("name");
        String description = (String) map.get("description");
        Priority priority = Priority.fromCode((String) map.get("priority"));
        String status = (String) map.get("status");
        String owner = (String) map.get("owner");
        long createdAt = map.containsKey("createdAt") ? ((Number) map.get("createdAt")).longValue() : 0L;
        long updatedAt = map.containsKey("updatedAt") ? ((Number) map.get("updatedAt")).longValue() : 0L;
        return new Project(id, name, description, priority, status, owner, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return String.format("Project[id=%s, name=%s, priority=%s]", getId(), name, priority);
    }
}
