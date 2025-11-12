package com.optistockplatrorm.dto;

import com.optistockplatrorm.util.EmailNotTaken;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record ManagerRequestDTO(

        @NotBlank(message = "Le nom de famille est obligatoire.")
        String lastName,

        @NotBlank(message = "Le prénom est obligatoire.")
        String firstName,

        @NotBlank(message = "L'adresse e-mail est obligatoire.")
        @Email(message = "L'adresse e-mail doit être valide.")
        @EmailNotTaken
        String email,

        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères.")
        String password,

        boolean active,

        Set<Long> IdWarehouse
) {}
