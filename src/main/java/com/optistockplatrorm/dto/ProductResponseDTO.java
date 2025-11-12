package com.optistockplatrorm.dto;

import java.time.LocalDateTime;

public record ProductResponseDTO(long id ,
                                 String categoryName,
                                 String name,
                                 String sku,
                                 double purchasePrice,
                                 double sellingPrice,
                                 boolean active,
                                 LocalDateTime createdAt
) {}
