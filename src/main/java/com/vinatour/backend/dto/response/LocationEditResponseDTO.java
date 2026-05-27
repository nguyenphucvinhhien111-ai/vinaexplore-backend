package com.vinatour.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationEditResponseDTO {
    private Integer id;
    private Integer locationId;
    private Integer userId;
    private String username;
    private String locationName; 
    private String newName;
    private String newDescription;
    private String newAddress;
    private Double newLatitude;
    private Double newLongitude;
    private String newCoverImage;
    private String status;
    private LocalDateTime createdAt;
}