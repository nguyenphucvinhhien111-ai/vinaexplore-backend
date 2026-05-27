package com.vinatour.backend.mapper;

import com.vinatour.backend.dto.request.UserRequestDTO;
import com.vinatour.backend.dto.response.UserResponseDTO;
import com.vinatour.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    User toUserEntity(UserRequestDTO requestDTO);
}