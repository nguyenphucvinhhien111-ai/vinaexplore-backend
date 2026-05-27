package com.vinatour.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private Integer id;
    private String comment;
    private Integer rating;
    private LocalDateTime createdAt;
    private String creatorUsername;
    private Integer creatorId;
    private String creatorAvatar;
    private String creatorFullName;
    private LocationResponseDTO location;
}