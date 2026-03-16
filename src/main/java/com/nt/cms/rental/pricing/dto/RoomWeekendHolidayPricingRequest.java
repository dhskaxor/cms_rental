package com.nt.cms.rental.pricing.dto;

import lombok.Data;

@Data
public class RoomWeekendHolidayPricingRequest {

    private Long roomId;
    /**
     * WEEKEND, HOLIDAY, BOTH
     */
    private String applyTo;
    private Integer unitMinutes;
    private Long price;
}

