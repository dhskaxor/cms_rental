package com.nt.cms.rental.calendar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentalCalendarSlotResponse {

    private String start;
    private String end;
    private boolean available;
    private String reason;
    private Long price;
    private String priceSource;
    private Long reservationId;
    private String reservationStatus;
}

