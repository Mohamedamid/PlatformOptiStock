package com.optistockplatrorm.dto;

import jakarta.validation.constraints.Positive;

public record PurchaseOrderReceptionRequestDTO(

        @Positive(message = "L’identifiant du produit doit être un nombre positif.")
        long productId,

        @Positive(message = "La quantité reçue doit être un nombre positif.")
        int receivedQty
) {}
