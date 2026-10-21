package com.tcet.pms.repository;

import com.tcet.pms.model.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * UserRepository implements the Repository pattern for User domain entities.
 * Combines in-memory fast caching using ConcurrentHashMap with disk persistence.
 */
public class UserRepository implements Repository<User> {
    private static final String FILE_NAME = "users.json";
    private final Map<String, User> storage = new ConcurrentHashMap<>();

    public UserRepository() {
        loadFromDisk();
    }

    private synchronized void loadFromDisk() {
        List<Map<String, Object>> records = FileDataStore.load(FILE_NAME);
        storage.clear();
        for (Map<String, Object> map : records) {
            User user = User.fromMap(map);
            storage.put(user.getId(), user);
        }
    }

    private synchronized void persist() {
        List<Map<String, Object>> records = storage.values().stream()
                .map(User::toMap)
                .collect(Collectors.toList());
        FileDataStore.save(FILE_NAME, records);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return storage.values().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst();
    }

    @Override
    public User save(User entity) {
        Objects.requireNonNull(entity, "User entity must not be null");
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
