package com.optistockplatrorm.mapper;

import com.optistockplatrorm.entity.Warehouse;
import com.optistockplatrorm.dto.WarehouseRequestDTO;
import com.optistockplatrorm.dto.WarehouseResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    WarehouseResponseDTO toDTO(Warehouse entity);
    Warehouse toEntity(WarehouseRequestDTO dto);
}
