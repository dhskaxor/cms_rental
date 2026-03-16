package com.nt.cms.rental.room.dto;

import lombok.Data;

@Data
public class RentalRoomUnavailableSlotRequest {

    private String scopeType;       // DATE_RANGE, WEEKLY_TIME, ONE_TIME
    private String startDateTime;   // yyyy-MM-ddTHH:mm, DATE_RANGE/ONE_TIME
    private String endDateTime;     // yyyy-MM-ddTHH:mm
    private Integer weekDay;        // WEEKLY_TIME
    private String startTime;       // HH:mm, WEEKLY_TIME
    private String endTime;         // HH:mm
    private String reason;
}

