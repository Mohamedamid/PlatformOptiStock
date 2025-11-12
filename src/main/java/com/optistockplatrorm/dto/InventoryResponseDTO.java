package com.optistockplatrorm.dto;

public record InventoryResponseDTO(
        Long id, int quantityOnHand, int quantityReserved, String productName, String warehouseName
) {}