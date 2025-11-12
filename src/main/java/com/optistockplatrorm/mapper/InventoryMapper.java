package com.optistockplatrorm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.optistockplatrorm.entity.Inventory;
import com.optistockplatrorm.dto.InventoryRequestDTO;
import com.optistockplatrorm.dto.InventoryResponseDTO;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, WarehouseMapper.class})
public interface InventoryMapper {

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    Inventory toEntity(InventoryRequestDTO dto);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    InventoryResponseDTO toDto(Inventory inventory);
}
