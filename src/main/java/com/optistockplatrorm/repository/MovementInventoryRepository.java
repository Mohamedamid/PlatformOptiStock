package com.optistockplatrorm.repository;

import com.optistockplatrorm.entity.InventoryMovement;
import com.optistockplatrorm.entity.Enums.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovementInventoryRepository extends JpaRepository<InventoryMovement, Long> {

    @Query("""
        SELECT COALESCE(SUM(mv.quantity), 0) FROM InventoryMovement mv
        JOIN mv.inventory inv
        JOIN PurchaseOrderLine ligne ON ligne.product = inv.product
        JOIN ligne.purchaseOrder commande
        WHERE inv.product.id = :produitId
          AND inv.warehouse.id = :warehouseId
          AND commande.id = :purchaseOrderId
          AND mv.movementType = :movementType
    """)

    Integer calculerQuantiteTotaleReçue(
            @Param("productId") Long produitId,
            @Param("warehouseId") Long warehouseId,
            @Param("purchaseOrderId") Long purchaseOrderId,
            @Param("movementType") MovementType movementType
    );
}
