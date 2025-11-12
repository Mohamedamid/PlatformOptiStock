package com.optistockplatrorm.mapper;

import com.optistockplatrorm.entity.Supplier;
import com.optistockplatrorm.dto.SupplierRequestDTO;
import com.optistockplatrorm.dto.SupplierResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier toEntity(SupplierRequestDTO dto);
    SupplierResponseDTO toDto(Supplier supplier);
    void toDtoSupplier(SupplierRequestDTO dto, @MappingTarget Supplier supplier);
}