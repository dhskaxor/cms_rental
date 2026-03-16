package com.nt.cms.rental.pricing.dto;

import lombok.Data;

@Data
public class RoomSpecialPricingRequest {

    private Long roomId;
    private String date;       // YYYY-MM-DD
    private String startTime;  // HH:mm
    private String endTime;    // HH:mm
    private Integer unitMinutes;
    private Long price;
}

