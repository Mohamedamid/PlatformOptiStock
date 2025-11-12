package com.optistockplatrorm.dto;

public record PurchaseOrderLineRsponseDTO (int quantity,
                                           double unitPrice,
                                           long productId,
                                           String productName
) {}