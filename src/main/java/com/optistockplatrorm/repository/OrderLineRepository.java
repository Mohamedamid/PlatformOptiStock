package com.optistockplatrorm.repository;

import com.optistockplatrorm.entity.Enums.OrderStatus;
import com.optistockplatrorm.entity.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderLineRepository extends JpaRepository<SalesOrderLine, Long> {
//    long countByProduct_SkuAndOrder_OrderStatusIn(String sku, List<OrderStatus> statuses);
}