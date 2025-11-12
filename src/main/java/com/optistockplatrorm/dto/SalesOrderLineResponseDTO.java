package com.optistockplatrorm.dto;

import com.optistockplatrorm.entity.Enums.OrderLineStatus;

public record SalesOrderLineResponseDTO(Long id,
                                        Long productId,
                                        String productName,
                                        Integer quantityRequested,
                                        Integer quantityBackorder,
                                        Integer quantityReserved,
                                        OrderLineStatus status,
                                        Double price
) {}