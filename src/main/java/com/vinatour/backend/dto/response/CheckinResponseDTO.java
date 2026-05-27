package com.vinatour.backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CheckinResponseDTO {
    private Integer id;
    private Integer locationId;
    private String locationName;
    private Double actualLatitude;
    private Double actualLongitude;
    private LocalDateTime checkinDate;
}