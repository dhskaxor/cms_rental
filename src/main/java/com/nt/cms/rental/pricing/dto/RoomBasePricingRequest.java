package com.nt.cms.rental.pricing.dto;

import lombok.Data;

@Data
public class RoomBasePricingRequest {

    private Long roomId;
    private Integer unitMinutes;
    private Long price;
}

