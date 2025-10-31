package com.optistockplatrorm.mapper;

import com.optistockplatrorm.entity.User;
import com.optistockplatrorm.dto.UserRequestDTO;
import com.optistockplatrorm.dto.UserResponseDTO;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toDto(User user);
    User toEntity(UserRequestDTO DTO);
}
