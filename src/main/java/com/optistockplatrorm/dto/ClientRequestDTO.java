package com.optistockplatrorm.dto;

import com.optistockplatrorm.util.EmailNotTaken;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ClientRequestDTO(
        @NotBlank(message = "name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @EmailNotTaken
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "\\+?\\d{10,15}", message = "Phone must be numeric and 10-15 digits")
        String phone

) {}
