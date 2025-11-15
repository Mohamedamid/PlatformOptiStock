package com.optistockplatrorm.mapper;

import com.optistockplatrorm.entity.SalesOrder;
import com.optistockplatrorm.dto.OrderResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SalesOrderLineMapper.class)
public interface SalesOrderMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "orderLines", target = "orderLines")
    OrderResponseDTO toDTO(SalesOrder order);
}