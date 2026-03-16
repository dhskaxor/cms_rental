package com.nt.cms.rental.room.controller;

import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.rental.room.dto.RentalRoomRequest;
import com.nt.cms.rental.room.dto.RentalRoomResponse;
import com.nt.cms.rental.room.service.RentalRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/rental/rooms")
@RequiredArgsConstructor
public class AdminRentalRoomController {

    private final RentalRoomService rentalRoomService;

    @PostMapping
    public ApiResponse<Long> createRoom(@RequestBody RentalRoomRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        Long id = rentalRoomService.createRoom(request, actorId);
        return ApiResponse.success(id);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateRoom(@PathVariable Long id,
                                        @RequestBody RentalRoomRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        rentalRoomService.updateRoom(id, request, actorId);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    public ApiResponse<RentalRoomResponse> getRoom(@PathVariable Long id) {
        return ApiResponse.success(rentalRoomService.getRoom(id));
    }

    @GetMapping
    public ApiResponse<List<RentalRoomResponse>> getRooms(@RequestParam(required = false) Long placeId) {
        if (placeId != null) {
            return ApiResponse.success(rentalRoomService.getRoomsByPlace(placeId));
        }
        return ApiResponse.success(rentalRoomService.getRooms());
    }
}

