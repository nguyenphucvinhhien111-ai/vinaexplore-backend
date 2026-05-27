package com.vinatour.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vinatour.backend.dto.request.ReviewRequestDTO;
import com.vinatour.backend.dto.response.ReviewResponseDTO;
import com.vinatour.backend.entity.Review;

@Mapper(componentModel = "spring", uses = { LocationMapper.class })
public interface ReviewMapper {

    @Mapping(source = "user.username", target = "creatorUsername")
    @Mapping(source = "user.id", target = "creatorId")
    @Mapping(source = "user.avatarUrl", target = "creatorAvatar")
    @Mapping(source = "user.fullName", target = "creatorFullName")
    ReviewResponseDTO toResponseDTO(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(ReviewRequestDTO requestDTO);
}