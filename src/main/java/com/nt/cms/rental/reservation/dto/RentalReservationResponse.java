package com.nt.cms.rental.reservation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentalReservationResponse {

    private Long id;
    private Long roomId;
    private Long userId;
    private String userName;
    private String start;
    private String end;
    private String status;
    private Long totalPrice;
    private String memo;
}

