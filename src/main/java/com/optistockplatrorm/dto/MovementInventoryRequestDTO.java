package com.optistockplatrorm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MovementInventoryRequestDTO(

        @NotNull(message = "L'identifiant de l'inventaire est obligatoire.")
        Long inventoryId,

        @Min(value = 1, message = "La quantité doit être au moins 1.")
        int quantity
) {}
