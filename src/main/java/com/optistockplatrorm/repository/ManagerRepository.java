package com.optistockplatrorm.repository;

import com.optistockplatrorm.entity.WarehouseManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<WarehouseManager,Long> {
}
