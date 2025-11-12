package com.optistockplatrorm.dto;

import jakarta.validation.constraints.Positive;

public record PurchaseOrderLineRequestDTO(
        @Positive(message = "La quantité doit être supérieure à 0.")
        int quantity,

        @Positive(message = "L’identifiant du produit doit être un nombre positif.")
        long productId
) {}
