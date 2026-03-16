package com.nt.cms.rental.publicapi.controller;

import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.rental.calendar.dto.RentalCalendarDayResponse;
import com.nt.cms.rental.calendar.dto.RentalCalendarRequest;
import com.nt.cms.rental.calendar.service.RentalCalendarService;
import com.nt.cms.rental.place.dto.RentalPlaceResponse;
import com.nt.cms.rental.place.service.RentalPlaceService;
import com.nt.cms.rental.room.dto.RentalRoomResponse;
import com.nt.cms.rental.room.service.RentalRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/rentals")
@RequiredArgsConstructor
public class PublicRentalController {

    private final RentalPlaceService rentalPlaceService;
    private final RentalRoomService rentalRoomService;
    private final RentalCalendarService rentalCalendarService;

    @GetMapping("/places")
    public ApiResponse<List<RentalPlaceResponse>> getPlaces() {
        return ApiResponse.success(rentalPlaceService.getPlaces());
    }

    @GetMapping("/places/{placeId}/rooms")
    public ApiResponse<List<RentalRoomResponse>> getRoomsByPlace(@PathVariable Long placeId) {
        return ApiResponse.success(rentalRoomService.getRoomsByPlace(placeId));
    }

    @GetMapping("/rooms/{roomId}/calendar")
    public ApiResponse<List<RentalCalendarDayResponse>> getRoomCalendar(@PathVariable Long roomId,
                                                                        @RequestParam Long placeId,
                                                                        @RequestParam String fromDate,
                                                                        @RequestParam String toDate,
                                                                        @RequestParam(required = false) Integer slotMinutes) {
        RentalCalendarRequest request = new RentalCalendarRequest();
        request.setPlaceId(placeId);
        request.setRoomId(roomId);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setSlotMinutes(slotMinutes);
        return ApiResponse.success(rentalCalendarService.getCalendar(request));
    }
}

