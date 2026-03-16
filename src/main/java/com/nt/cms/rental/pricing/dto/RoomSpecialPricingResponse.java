package com.nt.cms.rental.pricing.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomSpecialPricingResponse {

    private Long id;
    private String date;
    private String startTime;
    private String endTime;
    private Integer unitMinutes;
    private Long price;
}

