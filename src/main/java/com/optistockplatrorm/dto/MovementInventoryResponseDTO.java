package com.optistockplatrorm.dto;

import java.time.LocalDateTime;
import com.optistockplatrorm.entity.Enums.MovementType;

public record MovementInventoryResponseDTO (
        Long id , int quantity, Long   inventoryId, MovementType movementType, LocalDateTime createdAt
) {}