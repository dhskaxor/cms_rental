package com.nt.cms.rental.pricing.mapper;

import com.nt.cms.rental.pricing.dto.RoomBasePricingResponse;
import com.nt.cms.rental.pricing.dto.RoomSpecialPricingResponse;
import com.nt.cms.rental.pricing.dto.RoomWeekendHolidayPricingResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RentalPricingMapper {

    RoomBasePricingResponse findBasePricingByRoomId(@Param("roomId") Long roomId);

    void upsertBasePricing(@Param("roomId") Long roomId,
                           @Param("unitMinutes") Integer unitMinutes,
                           @Param("price") Long price);

    RoomWeekendHolidayPricingResponse findWeekendHolidayPricingByRoomId(@Param("roomId") Long roomId);

    void upsertWeekendHolidayPricing(@Param("roomId") Long roomId,
                                     @Param("applyTo") String applyTo,
                                     @Param("unitMinutes") Integer unitMinutes,
                                     @Param("price") Long price);

    List<RoomSpecialPricingResponse> findSpecialPricingByRoomId(@Param("roomId") Long roomId);

    void insertSpecialPricing(@Param("roomId") Long roomId,
                              @Param("date") String date,
                              @Param("startTime") String startTime,
                              @Param("endTime") String endTime,
                              @Param("unitMinutes") Integer unitMinutes,
                              @Param("price") Long price);

    void updateSpecialPricing(@Param("id") Long id,
                              @Param("roomId") Long roomId,
                              @Param("date") String date,
                              @Param("startTime") String startTime,
                              @Param("endTime") String endTime,
                              @Param("unitMinutes") Integer unitMinutes,
                              @Param("price") Long price);

    void deleteSpecialPricing(@Param("id") Long id, @Param("roomId") Long roomId);
}

