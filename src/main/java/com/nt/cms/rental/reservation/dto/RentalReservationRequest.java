package com.nt.cms.rental.reservation.dto;

import lombok.Data;

@Data
public class RentalReservationRequest {

    private Long roomId;
    private String start; // ISO-8601 datetime
    private String end;   // ISO-8601 datetime
    private String memo;
}

