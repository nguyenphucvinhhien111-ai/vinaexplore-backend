package com.vinatour.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LocationResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String coverImage;
    private String status;
    private LocalDateTime createdAt;
    private String creatorUsername;
    private Integer creatorId;
    private String creatorAvatarUrl;
    private String creatorFullName;
    private List<String> tags;
    private Integer checkinCount;
    private Double rating;
    private Integer reviewsCount;
}
