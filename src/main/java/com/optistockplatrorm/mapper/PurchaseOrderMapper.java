package com.optistockplatrorm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.optistockplatrorm.entity.PurchaseOrder;
import com.optistockplatrorm.dto.PurchaseOrderRequestDTO;
import com.optistockplatrorm.dto.PurchaseOrderResponseDTO;

@Mapper(componentModel = "spring" ,  uses = PurchaseOrderLineMapper.class)
public interface PurchaseOrderMapper {
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    PurchaseOrderResponseDTO toDto(PurchaseOrder purchaseOrder);
    PurchaseOrder toEntity(PurchaseOrderRequestDTO purchaseOrderRequestDTO);
}