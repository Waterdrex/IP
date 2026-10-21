package com.tcet.pms.repository;

import com.tcet.pms.model.Status;
import com.tcet.pms.model.Task;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * TaskRepository handles CRUD operations and filtering for Kanban tasks.
 */
public class TaskRepository implements Repository<Task> {
    private static final String FILE_NAME = "tasks.json";
    private final Map<String, Task> storage = new ConcurrentHashMap<>();

    public TaskRepository() {
        loadFromDisk();
    }

    private synchronized void loadFromDisk() {
        List<Map<String, Object>> records = FileDataStore.load(FILE_NAME);
        storage.clear();
        for (Map<String, Object> map : records) {
            Task t = Task.fromMap(map);
            storage.put(t.getId(), t);
        }
    }

    private synchronized void persist() {
        List<Map<String, Object>> records = storage.values().stream()
                .map(Task::toMap)
                .collect(Collectors.toList());
        FileDataStore.save(FILE_NAME, records);
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Task> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    public List<Task> findByProjectId(String projectId) {
        if (projectId == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(t -> projectId.equals(t.getProjectId()))
                .collect(Collectors.toList());
    }

    public List<Task> findByStatus(Status status) {
        return storage.values().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void deleteByProjectId(String projectId) {
        if (projectId == null) return;
        List<String> toRemove = storage.values().stream()
                .filter(t -> projectId.equals(t.getProjectId()))
                .map(Task::getId)
                .collect(Collectors.toList());
        for (String id : toRemove) {
            storage.remove(id);
        }
        if (!toRemove.isEmpty()) {
            persist();
        }
    }

    @Override
    public Task save(Task entity) {
        Objects.requireNonNull(entity, "Task cannot be null");
        storage.put(entity.getId(), entity);
        persist();
        return entity;
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) return false;
        boolean removed = storage.remove(id) != null;
        if (removed) {
            persist();
        }
        return removed;
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public void clear() {
        storage.clear();
        persist();
    }
}
