package com.optistockplatrorm.repository;

import com.optistockplatrorm.dto.WarehouseInventoryInfo;
import com.optistockplatrorm.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WareHouseRepository extends CrudRepository<Warehouse, Long> {
    List<Warehouse> findAllById(Iterable<Long> ID);
    Page<Warehouse> findAll(Pageable pageable);
    @Query("SELECT w FROM Warehouse w WHERE w.id = :id")
    List<Warehouse> findWareHouse(@Param("id") long id);
}
