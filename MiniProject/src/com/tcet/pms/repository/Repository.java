package com.tcet.pms.repository;

import com.tcet.pms.model.BaseEntity;
import java.util.List;
import java.util.Optional;

/**
 * Generic Repository interface defining the standard CRUD contract.
 * Demonstrates OOP: Abstraction, Interface, and Java Generics (<T extends BaseEntity>).
 */
public interface Repository<T extends BaseEntity> {
    List<T> findAll();
    Optional<T> findById(String id);
    T save(T entity);
    boolean deleteById(String id);
    long count();
    void clear();
}
