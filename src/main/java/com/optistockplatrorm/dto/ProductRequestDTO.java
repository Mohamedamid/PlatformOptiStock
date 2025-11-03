package com.optistockplatrorm.dto;

import jakarta.validation.constraints.*;

public record ProductRequestDTO(

        @NotBlank(message = "Le nom du produit est obligatoire.")
        @Size(min = 3, max = 100, message = "Le nom du produit doit contenir entre 3 et 100 caractères.")
        String name,

        @NotBlank(message = "Le code SKU est obligatoire.")
        @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Le code SKU ne doit contenir que des lettres majuscules, des chiffres, des tirets ou des underscores.")
        String sku,

        @PositiveOrZero(message = "Le prix d'achat doit être supérieur ou égal à 0.")
        double purchasePrice,

        @Positive(message = "Le prix de vente doit être supérieur à 0.")
        double sellingPrice,

        boolean active,

        @NotNull(message = "L'identifiant de la catégorie est obligatoire.")
        @Positive(message = "L'identifiant de la catégorie doit être un nombre positif.")
        Long categoryId
) {}
