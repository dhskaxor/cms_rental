package com.nt.cms.rental.room.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentalRoomUnavailableSlotResponse {

    private Long id;
    private Long roomId;
    private String scopeType;
    private String startDateTime;
    private String endDateTime;
    private Integer weekDay;
    private String startTime;
    private String endTime;
    private String reason;
}

