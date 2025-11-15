package com.optistockplatrorm.dto;

import com.optistockplatrorm.entity.Enums.OrderStatus;

import java.util.List;

public record OrderResponseDTO(long id,
                               long clientId,
                               OrderStatus orderStatus,
                               List<SalesOrderLineResponseDTO>orderLines,
                               String message
) {}