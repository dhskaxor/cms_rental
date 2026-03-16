package com.nt.cms.rental.reservation.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.rental.reservation.dto.RentalReservationRequest;
import com.nt.cms.rental.reservation.dto.RentalReservationResponse;
import com.nt.cms.rental.reservation.service.RentalReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rental/reservations")
@RequiredArgsConstructor
public class RentalReservationPublicController {

    private final RentalReservationService rentalReservationService;

    @PostMapping
    public ApiResponse<Long> createReservation(@RequestBody RentalReservationRequest request,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long id = rentalReservationService.createReservation(request, userDetails.getUserId());
        return ApiResponse.success(id);
    }

    @GetMapping("/my")
    public ApiResponse<List<RentalReservationResponse>> getMyReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(rentalReservationService.getMyReservations(userDetails.getUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<RentalReservationResponse> getReservation(@PathVariable Long id,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(rentalReservationService.getReservation(id, userDetails.getUserId()));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelMyReservation(@PathVariable Long id,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        rentalReservationService.cancelByUser(id, userDetails.getUserId());
        return ApiResponse.success();
    }
}

