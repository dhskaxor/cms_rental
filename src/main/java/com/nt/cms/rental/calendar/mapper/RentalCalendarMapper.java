package com.nt.cms.rental.calendar.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface RentalCalendarMapper {

    List<Map<String, Object>> findPlaceClosedRules(@Param("placeId") Long placeId,
                                                   @Param("fromDate") LocalDate fromDate,
                                                   @Param("toDate") LocalDate toDate);

    List<Map<String, Object>> findRoomUnavailableSlots(@Param("roomId") Long roomId,
                                                       @Param("fromDateTime") LocalDateTime from,
                                                       @Param("toDateTime") LocalDateTime to);

    List<Map<String, Object>> findBasePricing(@Param("roomId") Long roomId);

    List<Map<String, Object>> findWeekendHolidayPricing(@Param("roomId") Long roomId);

    List<Map<String, Object>> findSpecialPricing(@Param("roomId") Long roomId,
                                                 @Param("fromDate") LocalDate fromDate,
                                                 @Param("toDate") LocalDate toDate);

    List<Map<String, Object>> findReservations(@Param("roomId") Long roomId,
                                               @Param("fromDateTime") LocalDateTime from,
                                               @Param("toDateTime") LocalDateTime to);
}

