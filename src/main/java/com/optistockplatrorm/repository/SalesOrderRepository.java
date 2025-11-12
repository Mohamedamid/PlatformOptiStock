package com.optistockplatrorm.repository;

import com.optistockplatrorm.entity.SalesOrder;
import com.optistockplatrorm.entity.Enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesOrderRepository  extends JpaRepository<SalesOrder,Long> {
    List<SalesOrder> findByOrderStatusAndCreatedAtAfter(OrderStatus orderStatus, LocalDateTime date);
}