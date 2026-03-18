package com.nt.cms.rental.pricing.service;

import com.nt.cms.rental.pricing.dto.RoomBasePricingRequest;
import com.nt.cms.rental.pricing.dto.RoomWeekendHolidayPricingRequest;
import com.nt.cms.rental.pricing.mapper.RentalPricingMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultRentalPricingService 테스트")
class DefaultRentalPricingServiceTest {

    @Mock
    private RentalPricingMapper rentalPricingMapper;

    @InjectMocks
    private DefaultRentalPricingService pricingService;

    @Nested
    @DisplayName("기본 요금")
    class BasePricingTest {

        @Test
        @DisplayName("기본 요금 업데이트 시 upsert가 호출된다")
        void updateBasePricing_callsUpsert() {
            // given
            RoomBasePricingRequest request = new RoomBasePricingRequest();
            request.setUnitMinutes(60);
            request.setPrice(10000L);

            // when
            pricingService.updateBasePricing(1L, request, 1L);

            // then
            verify(rentalPricingMapper).upsertBasePricing(1L, 60, 10000L);
        }
    }

    @Nested
    @DisplayName("주말/공휴일 요금")
    class WeekendHolidayPricingTest {

        @Test
        @DisplayName("기존 데이터가 있으면 update만 호출된다")
        void updateWeekendHolidayPricing_whenUpdated_gtZero() {
            // given
            RoomWeekendHolidayPricingRequest request = new RoomWeekendHolidayPricingRequest();
            request.setApplyTo("WEEKEND");
            request.setUnitMinutes(60);
            request.setPrice(15000L);

            given(rentalPricingMapper.updateWeekendHolidayPricing(eq(1L), eq("WEEKEND"), anyInt(), anyLong()))
                    .willReturn(1);

            // when
            pricingService.updateWeekendHolidayPricing(1L, request, 1L);

            // then
            verify(rentalPricingMapper).updateWeekendHolidayPricing(1L, "WEEKEND", 60, 15000L);
        }

        @Test
        @DisplayName("기존 데이터가 없으면 insert가 호출된다")
        void updateWeekendHolidayPricing_whenUpdated_zero_inserts() {
            // given
            RoomWeekendHolidayPricingRequest request = new RoomWeekendHolidayPricingRequest();
            request.setApplyTo("WEEKEND");
            request.setUnitMinutes(60);
            request.setPrice(15000L);

            given(rentalPricingMapper.updateWeekendHolidayPricing(eq(1L), eq("WEEKEND"), anyInt(), anyLong()))
                    .willReturn(0);

            // when
            pricingService.updateWeekendHolidayPricing(1L, request, 1L);

            // then
            verify(rentalPricingMapper).insertWeekendHolidayPricing(1L, "WEEKEND", 60, 15000L);
        }
    }
}

