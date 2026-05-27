package com.vinatour.backend.mapper;

import java.util.*;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vinatour.backend.dto.request.LocationRequestDTO;
import com.vinatour.backend.dto.response.LocationResponseDTO;
import com.vinatour.backend.entity.Location;
import com.vinatour.backend.entity.Tag;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    @Mapping(source = "user.username", target = "creatorUsername")
    @Mapping(source = "user.id", target = "creatorId")
    @Mapping(source = "user.avatarUrl", target = "creatorAvatarUrl")
    @Mapping(source = "user.fullName", target = "creatorFullName")
    LocationResponseDTO toResponseDTO(Location location);

    default List<String> mapTagstoString(Set<Tag> tags) {
        if (tags == null)
            return List.of();
        return tags.stream().map(Tag::getName).collect(Collectors.toList());
    }

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "checkinCount", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "reviewsCount", ignore = true)
    @Mapping(target = "favoriteCount", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "checkins", ignore = true)
    Location toEntity(LocationRequestDTO requestDTO);
}
