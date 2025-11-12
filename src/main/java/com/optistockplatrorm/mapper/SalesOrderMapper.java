package com.optistockplatrorm.mapper;

import org.mapstruct.Mapper;
import com.optistockplatrorm.entity.SalesOrder;
import com.optistockplatrorm.dto.SalesOrderResponseDTO;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SalesOrderLineMapper.class)
public interface SalesOrderMapper {
    @Mapping(source = "client.id", target = "clientId")
    SalesOrderResponseDTO toDTO(SalesOrder order);
}