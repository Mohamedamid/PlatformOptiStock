package com.optistockplatrorm.dto;

import com.optistockplatrorm.entity.Enums.Role;

public record UserResponseDTO (Long id, String name, String email, Role role) {}
