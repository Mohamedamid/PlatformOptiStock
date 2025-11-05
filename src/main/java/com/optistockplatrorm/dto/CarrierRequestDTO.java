package com.optistockplatrorm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CarrierRequestDTO(
        @NotBlank(message = "Le nom du transporteur est obligatoire")
        @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
        String carrierName,

        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        String phoneNumber
) {}