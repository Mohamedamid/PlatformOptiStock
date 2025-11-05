package com.optistockplatrorm.dto;

import jakarta.validation.constraints.NotBlank;

public record WarehouseRequestDTO (
        @NotBlank(message = "Le nom de l'entrepôt est requis")
        String name,

        @NotBlank(message = "L'adresse est requise")
        String address,

        @NotBlank(message = "Le code de l'entrepôt est requis")
        String code,

        boolean active
) {}
