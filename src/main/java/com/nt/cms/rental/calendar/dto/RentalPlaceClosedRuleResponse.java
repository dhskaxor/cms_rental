package com.nt.cms.rental.calendar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentalPlaceClosedRuleResponse {

    private Long id;
    private Long placeId;
    private String ruleType;
    private String startDate;
    private String endDate;
    private Integer weekDay;
    private String holidayName;
}

