package com.optistockplatrorm.dto;

import com.optistockplatrorm.dto.PurchaseOrderLineRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderRequestDTO(

        @Positive(message = "L’identifiant du fournisseur doit être un nombre positif.")
        long supplierId,

        @Positive(message = "L’identifiant de l’entrepôt doit être un nombre positif.")
        long warehouseId,

        @NotNull(message = "La date prévue est obligatoire.")
        @FutureOrPresent(message = "La date prévue doit être aujourd’hui ou dans le futur.")
        LocalDateTime expectedDate,

        @NotNull(message = "Les lignes de commande d’achat sont obligatoires.")
        @Size(min = 1, message = "Au moins une ligne de commande d’achat est requise.")
        List<@Valid PurchaseOrderLineRequestDTO> liens
) {}