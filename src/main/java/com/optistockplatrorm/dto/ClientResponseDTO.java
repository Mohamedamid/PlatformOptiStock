package com.optistockplatrorm.dto;

import java.time.LocalDateTime;

public record  ClientResponseDTO(Long id, String name, String email, LocalDateTime createdAt, String phone, String role, boolean active)
{}
