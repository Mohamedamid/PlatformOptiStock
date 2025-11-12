package com.optistockplatrorm.dto;

import jakarta.validation.constraints.NotNull;

public record InventoryRequestDTO(

        @NotNull(message = "L'identifiant du produit doit être fourni.")
        Long productId,

        @NotNull(message = "L'identifiant de l'entrepôt doit être fourni.")
        Long warehouseId
) {}