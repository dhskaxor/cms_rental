package com.nt.cms.rental.room.dto;

import lombok.Data;

@Data
public class RentalRoomRequest {

    private Long placeId;
    private String name;
    private String description;
    private Integer capacity;
    private Integer defaultDurationMinutes;

    private Long basePrice;
    private Long weekendPrice;
    private String weekendApplyTo;
}

