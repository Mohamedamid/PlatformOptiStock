package com.optistockplatrorm.repository;

import com.optistockplatrorm.dto.WarehouseInventoryInfo;
import com.optistockplatrorm.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface WareHouseRepository extends JpaRepository<Warehouse, Long> {

    @Query("""
        SELECT w.id AS warehouseId, i.id AS inventoryId, (i.quantityOnHand - i.quantityReserved) AS quantityHand
        FROM Warehouse w JOIN w.inventory i JOIN i.product p WHERE p.id = :id
    """)
    List<WarehouseInventoryInfo> findWareHouse(@Param("id") long id);

    Page<Warehouse> findAll(Pageable pageable);

    List<Warehouse> findAllById(Iterable<Long> id);
}