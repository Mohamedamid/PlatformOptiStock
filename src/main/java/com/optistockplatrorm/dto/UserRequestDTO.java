package com.optistockplatrorm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record UserRequestDTO (@NotBlank(message = "Email is required")
                              @Email(message = "Email should be valid")
                              String email,
                              @NotBlank(message = "Password is required")
                              @Size(min = 6, message = "Password must be at least 6 characters")
                              String password) {
}
