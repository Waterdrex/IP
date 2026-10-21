package com.tcet.pms.repository;

import com.tcet.pms.model.Project;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ProjectRepository provides CRUD and querying capabilities for Projects.
 */
public class ProjectRepository implements Repository<Project> {
    private static final String FILE_NAME = "projects.json";
    private final Map<String, Project> storage = new ConcurrentHashMap<>();

    public ProjectRepository() {
        loadFromDisk();
    }

    private synchronized void loadFromDisk() {
        List<Map<String, Object>> records = FileDataStore.load(FILE_NAME);
        storage.clear();
        for (Map<String, Object> map : records) {
            Project p = Project.fromMap(map);
            storage.put(p.getId(), p);
        }
    }

    private synchronized void persist() {
        List<Map<String, Object>> records = storage.values().stream()
                .map(Project::toMap)
                .collect(Collectors.toList());
        FileDataStore.save(FILE_NAME, records);
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Project> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Project save(Project entity) {
        Objects.requireNonNull(entity, "Project cannot be null");
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
