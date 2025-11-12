package com.optistockplatrorm.dto;

import com.optistockplatrorm.entity.Enums.Role;

public record UserResponseDTO (Long id, String firstName, String lastName, String email, Role role) {}
