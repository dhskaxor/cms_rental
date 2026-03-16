package com.nt.cms.rental.place.controller;

import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.rental.place.dto.RentalPlaceRequest;
import com.nt.cms.rental.place.dto.RentalPlaceResponse;
import com.nt.cms.rental.place.service.RentalPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/rental/places")
@RequiredArgsConstructor
public class AdminRentalPlaceController {

    private final RentalPlaceService rentalPlaceService;

    @PostMapping
    public ApiResponse<Long> createPlace(@RequestBody RentalPlaceRequest request,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        Long id = rentalPlaceService.createPlace(request, actorId);
        return ApiResponse.success(id);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updatePlace(@PathVariable Long id,
                                         @RequestBody RentalPlaceRequest request,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        rentalPlaceService.updatePlace(id, request, actorId);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    public ApiResponse<RentalPlaceResponse> getPlace(@PathVariable Long id) {
        return ApiResponse.success(rentalPlaceService.getPlace(id));
    }

    @GetMapping
    public ApiResponse<List<RentalPlaceResponse>> getPlaces() {
        return ApiResponse.success(rentalPlaceService.getPlaces());
    }
}

