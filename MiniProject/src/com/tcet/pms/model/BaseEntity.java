package com.tcet.pms.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * BaseEntity represents the foundational abstract class in our domain hierarchy.
 * Demonstrates OOP Concept: Abstraction & Inheritance.
 * All domain models (Project, Task, User) inherit common fields and lifecycle methods.
 */
public abstract class BaseEntity {
    private String id;
    private long createdAt;
    private long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = this.createdAt;
    }

    public BaseEntity(String id, long createdAt, long updatedAt) {
        this.id = (id == null || id.trim().isEmpty()) ? UUID.randomUUID().toString().substring(0, 8) : id;
        this.createdAt = createdAt > 0 ? createdAt : Instant.now().toEpochMilli();
        this.updatedAt = updatedAt > 0 ? updatedAt : this.createdAt;
    }

    // Encapsulation: Controlled access through getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.id = id;
        }
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void touch() {
        this.updatedAt = Instant.now().toEpochMilli();
    }

    // Polymorphism: Common identity contract
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
