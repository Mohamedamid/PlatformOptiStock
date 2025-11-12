package com.optistockplatrorm.mapper;

import com.optistockplatrorm.entity.InventoryMovement;
import com.optistockplatrorm.dto.MovementInventoryRequestDTO;
import com.optistockplatrorm.dto.MovementInventoryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovementInventoryMapper {
    @Mapping(target = "inventoryId", source = "inventory.id")
    MovementInventoryResponseDTO toDTO(InventoryMovement entity);

    InventoryMovement toEntity(MovementInventoryRequestDTO dto);
}
