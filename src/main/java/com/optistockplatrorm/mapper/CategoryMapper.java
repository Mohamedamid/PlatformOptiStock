package com.optistockplatrorm.mapper;

import org.mapstruct.Mapper;
import com.optistockplatrorm.entity.Category;
import com.optistockplatrorm.dto.CategoryRequestDTO;
import com.optistockplatrorm.dto.CategoryResponseDTO;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDTO toDto(Category category);
    Category  toEntity(CategoryRequestDTO dto);
}
