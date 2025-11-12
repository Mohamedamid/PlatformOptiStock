package com.optistockplatrorm.repository;

import com.optistockplatrorm.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier,Long> {
    boolean existsByNumber(String Number);
}