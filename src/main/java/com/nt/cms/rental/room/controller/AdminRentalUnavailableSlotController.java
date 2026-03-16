package com.nt.cms.rental.room.controller;

import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.rental.room.dto.RentalRoomUnavailableSlotRequest;
import com.nt.cms.rental.room.dto.RentalRoomUnavailableSlotResponse;
import com.nt.cms.rental.room.service.RentalUnavailableSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/rental/rooms/{roomId}/unavailable-slots")
@RequiredArgsConstructor
public class AdminRentalUnavailableSlotController {

    private final RentalUnavailableSlotService rentalUnavailableSlotService;

    @GetMapping
    public ApiResponse<List<RentalRoomUnavailableSlotResponse>> list(@PathVariable Long roomId,
                                                                     @RequestParam String fromDateTime,
                                                                     @RequestParam String toDateTime) {
        return ApiResponse.success(rentalUnavailableSlotService.getSlots(roomId, fromDateTime, toDateTime));
    }

    @PostMapping
    public ApiResponse<Void> create(@PathVariable Long roomId,
                                    @RequestBody RentalRoomUnavailableSlotRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        rentalUnavailableSlotService.createSlot(roomId, request, actorId);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long roomId,
                                    @PathVariable Long id,
                                    @RequestBody RentalRoomUnavailableSlotRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        rentalUnavailableSlotService.updateSlot(id, roomId, request, actorId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long roomId,
                                    @PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        rentalUnavailableSlotService.deleteSlot(id, roomId, actorId);
        return ApiResponse.success();
    }
}

