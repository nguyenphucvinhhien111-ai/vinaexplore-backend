package com.vinatour.backend.dto.request;

import lombok.Data;

@Data
public class CheckinRequestDTO {
    private Integer userId;
    private Integer locationId;
    private Double actualLatitude;
    private Double actualLongitude;
}