package com.optistockplatrorm.repository;

import com.optistockplatrorm.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository  extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndWarehouseId(long productId, long warehouseId);
    List<Inventory> findByProductSku(String sku);
}