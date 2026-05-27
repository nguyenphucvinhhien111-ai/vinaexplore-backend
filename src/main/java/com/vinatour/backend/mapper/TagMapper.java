package com.vinatour.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vinatour.backend.dto.request.TagRequestDTO;
import com.vinatour.backend.dto.response.TagResponseDTO;
import com.vinatour.backend.entity.Tag;

@Mapper(componentModel="spring")
public interface TagMapper {
    TagResponseDTO toresponseDTO(Tag tag);

    @Mapping (target ="id", ignore =true)
    @Mapping (target= "locations", ignore=true)
    Tag toentity(TagRequestDTO requestDTO);
    
}
