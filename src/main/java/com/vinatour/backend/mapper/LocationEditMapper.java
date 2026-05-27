package com.vinatour.backend.mapper;

import com.vinatour.backend.dto.request.LocationEditRequestDTO;
import com.vinatour.backend.dto.response.LocationEditResponseDTO;
import com.vinatour.backend.entity.LocationEdit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationEditMapper {

    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "locationName", source = "location.name")
    LocationEditResponseDTO toResponseDTO(LocationEdit locationEdit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    LocationEdit toEntity(LocationEditRequestDTO requestDTO);
}