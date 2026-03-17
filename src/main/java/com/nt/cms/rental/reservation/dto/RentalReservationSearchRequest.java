package com.nt.cms.rental.reservation.dto;

import lombok.Data;

@Data
public class RentalReservationSearchRequest {

    private Long roomId;
    private String from;  // ISO-8601 datetime or date
    private String to;    // ISO-8601 datetime or date
    private String status;
    private String userName;  // 예약자명 (LIKE 검색)
}

