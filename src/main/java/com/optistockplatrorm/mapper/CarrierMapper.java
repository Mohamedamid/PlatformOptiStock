package com.optistockplatrorm.mapper;

import com.optistockplatrorm.entity.Carrier;
import com.optistockplatrorm.dto.CarrierRequestDTO;
import com.optistockplatrorm.dto.CarrierResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarrierMapper {
    Carrier toEntity(CarrierRequestDTO dto);
    CarrierResponseDTO toDTO(Carrier carrier);
}
