package com.nt.cms.rental.pricing.service;

import com.nt.cms.rental.pricing.dto.RoomBasePricingRequest;
import com.nt.cms.rental.pricing.dto.RoomBasePricingResponse;
import com.nt.cms.rental.pricing.dto.RoomSpecialPricingRequest;
import com.nt.cms.rental.pricing.dto.RoomSpecialPricingResponse;
import com.nt.cms.rental.pricing.dto.RoomWeekendHolidayPricingRequest;
import com.nt.cms.rental.pricing.dto.RoomWeekendHolidayPricingResponse;

import java.util.List;

public interface RentalPricingService {

    RoomBasePricingResponse getBasePricing(Long roomId);

    void updateBasePricing(Long roomId, RoomBasePricingRequest request, Long actorId);

    RoomWeekendHolidayPricingResponse getWeekendHolidayPricing(Long roomId);

    void updateWeekendHolidayPricing(Long roomId, RoomWeekendHolidayPricingRequest request, Long actorId);

    List<RoomSpecialPricingResponse> getSpecialPricingList(Long roomId);

    Long createSpecialPricing(Long roomId, RoomSpecialPricingRequest request, Long actorId);

    void updateSpecialPricing(Long id, Long roomId, RoomSpecialPricingRequest request, Long actorId);

    void deleteSpecialPricing(Long id, Long roomId, Long actorId);
}

