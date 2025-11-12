package com.optistockplatrorm.repository;

import com.optistockplatrorm.entity.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Long> {}