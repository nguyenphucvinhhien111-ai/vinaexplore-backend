package com.vinatour.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Integer id;
    private Integer userId;
    private Integer senderId;
    private String senderName; 
    private String type;
    private Integer referenceId;
    private String message;
    private Boolean isRead;
    private java.util.Date createdAt;
}