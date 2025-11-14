package com.optistockplatrorm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.optistockplatrorm.entity.SalesOrderLine;
import com.optistockplatrorm.dto.SalesOrderLineResponseDTO;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SalesOrderLineMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    SalesOrderLineResponseDTO toDTO(SalesOrderLine line);
//    List<SalesOrderLineResponseDTO> toDTO(List<SalesOrderLine> lines);
    List<SalesOrderLineResponseDTO> toDTOs(List<SalesOrderLine> lines);

}
