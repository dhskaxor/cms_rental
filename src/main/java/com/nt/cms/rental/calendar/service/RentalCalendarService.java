package com.nt.cms.rental.calendar.service;

import com.nt.cms.rental.calendar.dto.RentalCalendarDayResponse;
import com.nt.cms.rental.calendar.dto.RentalCalendarRequest;

import java.util.List;

public interface RentalCalendarService {

    List<RentalCalendarDayResponse> getCalendar(RentalCalendarRequest request);
}

