package com.nt.cms.rental.calendar.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RentalCalendarDayResponse {

    private String date;
    private String dayType;
    private String holidayName;
    private List<RentalCalendarSlotResponse> slots;
    private List<ReservationSummary> reservations;
}

