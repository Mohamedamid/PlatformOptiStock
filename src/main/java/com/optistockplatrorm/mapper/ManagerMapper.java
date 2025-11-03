package com.optistockplatrorm.mapper;

import com.optistockplatrorm.dto.ManagerRequestDTO;
import com.optistockplatrorm.dto.ManagerResponseDTO;
import com.optistockplatrorm.entity.WarehouseManager;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ManagerMapper {
    ManagerResponseDTO toDTO(WarehouseManager manager);
    WarehouseManager toEntity(ManagerRequestDTO managerRequestDTO);
}
