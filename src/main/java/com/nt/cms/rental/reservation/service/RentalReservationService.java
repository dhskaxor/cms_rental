package com.nt.cms.rental.reservation.service;

import com.nt.cms.rental.reservation.dto.RentalReservationRequest;
import com.nt.cms.rental.reservation.dto.RentalReservationResponse;
import com.nt.cms.rental.reservation.dto.RentalReservationSearchRequest;
import com.nt.cms.rental.reservation.dto.RentalReservationSearchResponse;

import java.util.List;

public interface RentalReservationService {

    Long createReservation(RentalReservationRequest request, Long userId);

    RentalReservationResponse getReservation(Long id, Long userId);

    RentalReservationResponse getReservationByAdmin(Long id);

    List<RentalReservationResponse> getMyReservations(Long userId);

    RentalReservationSearchResponse searchReservations(RentalReservationSearchRequest request);

    void cancelByUser(Long id, Long userId);

    void confirmByAdmin(Long id, Long actorId);

    void rejectByAdmin(Long id, Long actorId);

    void cancelByAdmin(Long id, Long actorId);
}

