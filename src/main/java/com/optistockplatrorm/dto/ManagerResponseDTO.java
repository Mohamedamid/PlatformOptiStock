package com.optistockplatrorm.dto;

import java.time.LocalDateTime;

public record ManagerResponseDTO(Long id, String name, String email, LocalDateTime createdAt, String role, boolean active
) {}
