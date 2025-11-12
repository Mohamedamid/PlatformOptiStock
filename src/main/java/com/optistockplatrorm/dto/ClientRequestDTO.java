package com.optistockplatrorm.dto;

import com.optistockplatrorm.util.EmailNotTaken;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ClientRequestDTO(

        @NotBlank(message = "Le nom de famille est obligatoire.")
        String lastName,

        @NotBlank(message = "Le prénom est obligatoire.")
        String firstName,

        @NotBlank(message = "L'adresse e-mail est obligatoire.")
        @Email(message = "L'adresse e-mail doit être valide.")
        @EmailNotTaken
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire.")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères.")
        String password,

        @NotBlank(message = "Le numéro de téléphone est obligatoire.")
        @Pattern(regexp = "\\+?\\d{10,15}", message = "Le numéro de téléphone doit contenir entre 10 et 15 chiffres, et peut commencer par '+'.")
        String phone
) {}
