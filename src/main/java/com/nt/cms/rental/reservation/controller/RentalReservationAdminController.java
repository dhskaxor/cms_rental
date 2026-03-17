package com.nt.cms.rental.reservation.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.rental.reservation.dto.RentalReservationResponse;
import com.nt.cms.rental.reservation.dto.RentalReservationSearchRequest;
import com.nt.cms.rental.reservation.dto.RentalReservationSearchResponse;
import com.nt.cms.rental.reservation.service.RentalReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/rental/reservations")
@RequiredArgsConstructor
public class RentalReservationAdminController {

    private final RentalReservationService rentalReservationService;

    @GetMapping
    public ApiResponse<RentalReservationSearchResponse> searchReservations(RentalReservationSearchRequest request) {
        return ApiResponse.success(rentalReservationService.searchReservations(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<RentalReservationResponse> getReservation(@PathVariable Long id) {
        return ApiResponse.success(rentalReservationService.getReservationByAdmin(id));
    }

    @PostMapping("/{id}/confirm")
    public ApiResponse<Void> confirmReservation(@PathVariable Long id,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        rentalReservationService.confirmByAdmin(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<Void> rejectReservation(@PathVariable Long id,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        rentalReservationService.rejectByAdmin(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelReservationByAdmin(@PathVariable Long id,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        rentalReservationService.cancelByAdmin(id, userDetails.getUserId());
        return ApiResponse.success();
    }
}

