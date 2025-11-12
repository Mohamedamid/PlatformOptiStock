package com.optistockplatrorm.mapper;

import com.optistockplatrorm.dto.ProductRequestDTO;
import com.optistockplatrorm.dto.ProductResponseDTO;
import com.optistockplatrorm.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponseDTO toDTO(Product product);
    Product  toEntity(ProductRequestDTO dto);
}
