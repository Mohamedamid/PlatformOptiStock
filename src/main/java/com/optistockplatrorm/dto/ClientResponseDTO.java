package com.optistockplatrorm.dto;

import java.time.LocalDateTime;

public record  ClientResponseDTO(Long id,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 LocalDateTime createdAt,
                                 String phone,
                                 String role,
                                 boolean active
) {}
