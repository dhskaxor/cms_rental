package com.nt.cms.rental.calendar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationSummary {

    private Long id;
    private String userName;
    private String startTime;  // HH:mm
    private String endTime;    // HH:mm
    private Long totalPrice;
    private String status;
}
