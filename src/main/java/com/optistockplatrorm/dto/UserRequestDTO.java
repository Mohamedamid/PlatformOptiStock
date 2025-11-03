package com.optistockplatrorm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(

        @NotBlank(message = "L'adresse e-mail est obligatoire.")
        @Email(message = "L'adresse e-mail doit être valide.")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire.")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères.")
        String password
) {}
