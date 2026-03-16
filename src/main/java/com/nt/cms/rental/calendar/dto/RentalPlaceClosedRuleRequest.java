package com.nt.cms.rental.calendar.dto;

import lombok.Data;

@Data
public class RentalPlaceClosedRuleRequest {

    private String ruleType;    // WEEKDAY, DATE, HOLIDAY
    private String startDate;   // yyyy-MM-dd
    private String endDate;     // yyyy-MM-dd (nullable)
    private Integer weekDay;    // 1~7
    private String holidayName;
}

