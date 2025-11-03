package com.optistockplatrorm.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(

        @NotBlank(message = "Le nom de la catégorie est obligatoire.")
        String name,

        String description,

        boolean active
) {}
