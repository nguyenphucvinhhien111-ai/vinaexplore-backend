package com.vinatour.backend.mapper;

import com.vinatour.backend.dto.response.NotificationResponseDTO;
import com.vinatour.backend.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderName", source = "sender.username")
    NotificationResponseDTO toResponseDTO(Notification notification);
}