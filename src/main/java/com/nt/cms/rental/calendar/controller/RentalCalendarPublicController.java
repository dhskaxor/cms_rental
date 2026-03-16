package com.nt.cms.rental.calendar.controller;

import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.rental.calendar.dto.RentalCalendarDayResponse;
import com.nt.cms.rental.calendar.dto.RentalCalendarRequest;
import com.nt.cms.rental.calendar.service.RentalCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rental/search")
@RequiredArgsConstructor
public class RentalCalendarPublicController {

    private final RentalCalendarService rentalCalendarService;

    @GetMapping
    public ApiResponse<List<RentalCalendarDayResponse>> searchAvailableSlots(RentalCalendarRequest request) {
        return ApiResponse.success(rentalCalendarService.getCalendar(request));
    }
}

