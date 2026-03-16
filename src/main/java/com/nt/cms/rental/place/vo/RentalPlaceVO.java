package com.nt.cms.rental.place.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RentalPlaceVO {

    private Long id;
    private String name;
    private String address;
    private String description;
    private String timeZone;
    private String openingTime;   // HH:mm, DB TIME 매핑
    private String closingTime;   // HH:mm

    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Boolean deleted;
}

