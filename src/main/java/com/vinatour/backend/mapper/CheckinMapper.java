package com.vinatour.backend.mapper;

import com.vinatour.backend.dto.response.CheckinResponseDTO;
import com.vinatour.backend.entity.Checkin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CheckinMapper {

    @Mapping(source = "location.id", target = "locationId")
    @Mapping(source = "location.name", target = "locationName")
    CheckinResponseDTO toResponseDTO(Checkin checkin);
}