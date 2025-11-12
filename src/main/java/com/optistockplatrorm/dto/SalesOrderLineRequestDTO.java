package com.optistockplatrorm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SalesOrderLineRequestDTO(

        @NotNull(message = "L’identifiant du produit doit être renseigné.")
        Long productId,

        @Positive(message = "La quantité demandée doit être supérieure à 0.")
        int quantityRequested

) {}
