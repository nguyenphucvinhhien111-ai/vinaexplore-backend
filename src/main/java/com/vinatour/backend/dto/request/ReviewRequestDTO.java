package com.vinatour.backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {
    private String comment;
    private Integer rating;
    private Integer userId;
    private Integer locationId;
}