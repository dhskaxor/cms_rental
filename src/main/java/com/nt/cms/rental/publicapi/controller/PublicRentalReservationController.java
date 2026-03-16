package com.nt.cms.rental.publicapi.controller;

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
@RequestMapping("/api/v1/public/rentals")
@RequiredArgsConstructor
public class PublicRentalReservationController {

    private final RentalReservationService rentalReservationService;

    @PostMapping("/rooms/{roomId}/reservations")
    public ApiResponse<Long> createReservation(@PathVariable Long roomId,
                                               @RequestBody RentalReservationRequest request,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        request.setRoomId(roomId);
        Long id = rentalReservationService.createReservation(request, userDetails.getUserId());
        return ApiResponse.success(id);
    }

    @GetMapping("/reservations/my")
    public ApiResponse<List<RentalReservationResponse>> getMyReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(rentalReservationService.getMyReservations(userDetails.getUserId()));
    }

    @GetMapping("/reservations/{id}")
    public ApiResponse<RentalReservationResponse> getReservation(@PathVariable Long id,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(rentalReservationService.getReservation(id, userDetails.getUserId()));
    }

    @DeleteMapping("/reservations/{id}")
    public ApiResponse<Void> cancelMyReservation(@PathVariable Long id,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        rentalReservationService.cancelByUser(id, userDetails.getUserId());
        return ApiResponse.success();
    }
}

