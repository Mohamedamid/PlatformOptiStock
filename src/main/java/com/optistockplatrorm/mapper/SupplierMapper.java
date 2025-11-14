package com.optistockplatrorm.mapper;

import com.optistockplatrorm.entity.Supplier;
import com.optistockplatrorm.dto.SupplierRequestDTO;
import com.optistockplatrorm.dto.SupplierResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    @Mapping(target = "number", source = "phoneNumber") // ← AJOUT IMPORTANT
    Supplier toEntity(SupplierRequestDTO dto);

    @Mapping(target = "phoneNumber", source = "number") // ← Pour la réponse aussi
    SupplierResponseDTO toDto(Supplier supplier);

    @Mapping(target = "number", source = "phoneNumber") // ← Pour l'update
    void toDtoSupplier(SupplierRequestDTO dto, @MappingTarget Supplier supplier);
}