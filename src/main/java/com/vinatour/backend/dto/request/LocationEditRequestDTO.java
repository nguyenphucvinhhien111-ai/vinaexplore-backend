package com.vinatour.backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationEditRequestDTO {
    private String newName;
    private String newDescription;
    private String newAddress;
    private Double newLatitude;
    private Double newLongitude;
    private String newCoverImage;
}