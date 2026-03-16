package com.nt.cms.rental.pricing.controller;

import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.rental.pricing.dto.RoomBasePricingRequest;
import com.nt.cms.rental.pricing.dto.RoomBasePricingResponse;
import com.nt.cms.rental.pricing.dto.RoomSpecialPricingRequest;
import com.nt.cms.rental.pricing.dto.RoomSpecialPricingResponse;
import com.nt.cms.rental.pricing.dto.RoomWeekendHolidayPricingRequest;
import com.nt.cms.rental.pricing.dto.RoomWeekendHolidayPricingResponse;
import com.nt.cms.rental.pricing.service.RentalPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/rental/rooms/{roomId}/pricing")
@RequiredArgsConstructor
public class AdminRentalPricingController {

    private final RentalPricingService rentalPricingService;

    @GetMapping("/base")
    public ApiResponse<RoomBasePricingResponse> getBasePricing(@PathVariable Long roomId) {
        return ApiResponse.success(rentalPricingService.getBasePricing(roomId));
    }

    @PutMapping("/base")
    public ApiResponse<Void> updateBasePricing(@PathVariable Long roomId,
                                               @RequestBody RoomBasePricingRequest request,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        request.setRoomId(roomId);
        rentalPricingService.updateBasePricing(roomId, request, actorId);
        return ApiResponse.success();
    }
    @GetMapping("/weekend-holiday")
    public ApiResponse<RoomWeekendHolidayPricingResponse> getWeekendHolidayPricing(@PathVariable Long roomId) {
        return ApiResponse.success(rentalPricingService.getWeekendHolidayPricing(roomId));
    }


    @PutMapping("/weekend-holiday")
    public ApiResponse<Void> updateWeekendHolidayPricing(@PathVariable Long roomId,
                                                         @RequestBody RoomWeekendHolidayPricingRequest request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        request.setRoomId(roomId);
        rentalPricingService.updateWeekendHolidayPricing(roomId, request, actorId);
        return ApiResponse.success();
    }
    @GetMapping("/special")
    public ApiResponse<java.util.List<RoomSpecialPricingResponse>> getSpecialPricing(@PathVariable Long roomId) {
        return ApiResponse.success(rentalPricingService.getSpecialPricingList(roomId));
    }


    @PostMapping("/special")
    public ApiResponse<Long> createSpecialPricing(@PathVariable Long roomId,
                                                  @RequestBody RoomSpecialPricingRequest request,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        request.setRoomId(roomId);
        Long id = rentalPricingService.createSpecialPricing(roomId, request, actorId);
        return ApiResponse.success(id);
    }

    @PutMapping("/special/{id}")
    public ApiResponse<Void> updateSpecialPricing(@PathVariable Long roomId,
                                                  @PathVariable Long id,
                                                  @RequestBody RoomSpecialPricingRequest request,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        request.setRoomId(roomId);
        rentalPricingService.updateSpecialPricing(id, roomId, request, actorId);
        return ApiResponse.success();
    }

    @DeleteMapping("/special/{id}")
    public ApiResponse<Void> deleteSpecialPricing(@PathVariable Long roomId,
                                                  @PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        rentalPricingService.deleteSpecialPricing(id, roomId, actorId);
        return ApiResponse.success();
    }
}

