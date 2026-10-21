package com.tcet.pms.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Task Entity representing an actionable work unit within a Project.
 * Demonstrates Inheritance (BaseEntity) and composition/association (references Project and User).
 */
public class Task extends BaseEntity {
    private String projectId;
    private String title;
    private String description;
    private Status status;       // TODO, IN_PROGRESS, COMPLETED
    private Priority priority;   // LOW, MEDIUM, HIGH
    private String assignee;     // Assigned user name or ID

    public Task() {
        super();
        this.status = Status.TODO;
        this.priority = Priority.MEDIUM;
    }

    public Task(String projectId, String title, String description, Priority priority, String assignee) {
        super();
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = Status.TODO;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.assignee = assignee;
    }

    public Task(String id, String projectId, String title, String description, Status status, Priority priority, String assignee, long createdAt, long updatedAt) {
        super(id, createdAt, updatedAt);
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status != null ? status : Status.TODO;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.assignee = assignee;
    }

    // Getters and Setters
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
        touch();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        touch();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        touch();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status != null ? status : Status.TODO;
        touch();
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority != null ? priority : Priority.MEDIUM;
        touch();
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
        touch();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", getId());
        map.put("projectId", projectId != null ? projectId : "");
        map.put("title", title != null ? title : "");
        map.put("description", description != null ? description : "");
        map.put("status", status != null ? status.getCode() : "todo");
        map.put("priority", priority != null ? priority.getCode() : "medium");
        map.put("assignee", assignee != null ? assignee : "");
        map.put("createdAt", getCreatedAt());
        map.put("updatedAt", getUpdatedAt());
        return map;
    }

    public static Task fromMap(Map<String, Object> map) {
        String id = (String) map.get("id");
        String projectId = (String) map.get("projectId");
        String title = (String) map.get("title");
        String description = (String) map.get("description");
        Status status = Status.fromCode((String) map.get("status"));
        Priority priority = Priority.fromCode((String) map.get("priority"));
        String assignee = (String) map.get("assignee");
        long createdAt = map.containsKey("createdAt") ? ((Number) map.get("createdAt")).longValue() : 0L;
        long updatedAt = map.containsKey("updatedAt") ? ((Number) map.get("updatedAt")).longValue() : 0L;
        return new Task(id, projectId, title, description, status, priority, assignee, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return String.format("Task[id=%s, title=%s, status=%s, priority=%s]", getId(), title, status, priority);
    }
}
