package com.nt.cms.rental.calendar.dto;

import lombok.Data;

@Data
public class RentalCalendarRequest {

    private Long placeId;
    private Long roomId;
    private String fromDate; // YYYY-MM-DD
    private String toDate;   // YYYY-MM-DD
    private Integer slotMinutes;
}

