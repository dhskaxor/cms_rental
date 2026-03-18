package com.nt.cms.rental.calendar.dto;

import lombok.Data;

@Data
public class RentalCalendarRequest {

    private Long placeId;
    private Long roomId;
    private String yearMonth; // YYYY-MM
}
