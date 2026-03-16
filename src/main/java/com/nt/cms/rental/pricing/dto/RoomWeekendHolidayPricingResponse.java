package com.nt.cms.rental.pricing.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomWeekendHolidayPricingResponse {

    private String applyTo;
    private Integer unitMinutes;
    private Long price;
}

