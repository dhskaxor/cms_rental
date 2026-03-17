package com.nt.cms.rental.reservation.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RentalReservationSearchResponse {

    private List<RentalReservationResponse> items;
    private Long totalAmount;
}
