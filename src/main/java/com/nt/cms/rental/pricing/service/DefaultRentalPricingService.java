package com.nt.cms.rental.pricing.service;

import com.nt.cms.rental.pricing.dto.RoomBasePricingRequest;
import com.nt.cms.rental.pricing.dto.RoomBasePricingResponse;
import com.nt.cms.rental.pricing.dto.RoomSpecialPricingRequest;
import com.nt.cms.rental.pricing.dto.RoomSpecialPricingResponse;
import com.nt.cms.rental.pricing.dto.RoomWeekendHolidayPricingRequest;
import com.nt.cms.rental.pricing.dto.RoomWeekendHolidayPricingResponse;
import com.nt.cms.rental.pricing.mapper.RentalPricingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultRentalPricingService implements RentalPricingService {

    private final RentalPricingMapper rentalPricingMapper;

    @Override
    public RoomBasePricingResponse getBasePricing(Long roomId) {
        return rentalPricingMapper.findBasePricingByRoomId(roomId);
    }

    @Override
    @Transactional
    public void updateBasePricing(Long roomId, RoomBasePricingRequest request, Long actorId) {
        rentalPricingMapper.upsertBasePricing(
                roomId,
                request.getUnitMinutes(),
                request.getPrice()
        );
    }

    @Override
    public RoomWeekendHolidayPricingResponse getWeekendHolidayPricing(Long roomId) {
        return rentalPricingMapper.findWeekendHolidayPricingByRoomId(roomId);
    }

    @Override
    @Transactional
    public void updateWeekendHolidayPricing(Long roomId, RoomWeekendHolidayPricingRequest request, Long actorId) {
        rentalPricingMapper.upsertWeekendHolidayPricing(
                roomId,
                request.getApplyTo(),
                request.getUnitMinutes(),
                request.getPrice()
        );
    }

    @Override
    public List<RoomSpecialPricingResponse> getSpecialPricingList(Long roomId) {
        return rentalPricingMapper.findSpecialPricingByRoomId(roomId);
    }

    @Override
    @Transactional
    public Long createSpecialPricing(Long roomId, RoomSpecialPricingRequest request, Long actorId) {
        rentalPricingMapper.insertSpecialPricing(
                roomId,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getUnitMinutes(),
                request.getPrice()
        );
        // auto increment PK는 mapper에서 직접 조회하지 않으므로, 필요 시 추후 조회 API에서 사용
        return null;
    }

    @Override
    @Transactional
    public void updateSpecialPricing(Long id, Long roomId, RoomSpecialPricingRequest request, Long actorId) {
        rentalPricingMapper.updateSpecialPricing(
                id,
                roomId,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getUnitMinutes(),
                request.getPrice()
        );
    }

    @Override
    @Transactional
    public void deleteSpecialPricing(Long id, Long roomId, Long actorId) {
        rentalPricingMapper.deleteSpecialPricing(id, roomId);
    }
}

