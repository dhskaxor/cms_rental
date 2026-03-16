package com.nt.cms.rental.pricing.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomBasePricingResponse {

    private Integer unitMinutes;
    private Long price;
}

