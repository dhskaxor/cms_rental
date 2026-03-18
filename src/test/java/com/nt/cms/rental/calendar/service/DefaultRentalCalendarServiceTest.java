package com.nt.cms.rental.calendar.service;

import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.rental.calendar.dto.RentalCalendarRequest;
import com.nt.cms.rental.calendar.dto.RentalCalendarDayResponse;
import com.nt.cms.rental.calendar.mapper.RentalCalendarMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultRentalCalendarService 테스트")
class DefaultRentalCalendarServiceTest {

    @Mock
    private RentalCalendarMapper rentalCalendarMapper;

    @InjectMocks
    private DefaultRentalCalendarService calendarService;

    @Nested
    @DisplayName("입력 검증")
    class ValidationTest {

        @Test
        @DisplayName("roomId가 없으면 예외 발생")
        void getCalendar_withoutRoomId_throwsException() {
            // given
            RentalCalendarRequest request = new RentalCalendarRequest();
            request.setYearMonth("2026-03");

            // when & then
            assertThatThrownBy(() -> calendarService.getCalendar(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
        }

        @Test
        @DisplayName("yearMonth가 없으면 예외 발생")
        void getCalendar_withoutYearMonth_throwsException() {
            // given
            RentalCalendarRequest request = new RentalCalendarRequest();
            request.setRoomId(1L);

            // when & then
            assertThatThrownBy(() -> calendarService.getCalendar(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    @Nested
    @DisplayName("기본 캘린더 생성")
    class BasicCalendarTest {

        @Test
        @DisplayName("데이터가 없어도 일 단위 응답을 반환한다")
        void getCalendar_returnsDaysEvenWhenEmpty() {
            // given
            YearMonth ym = YearMonth.of(2026, 3);
            LocalDate from = ym.atDay(1);
            LocalDate to = ym.atEndOfMonth();

            RentalCalendarRequest request = new RentalCalendarRequest();
            request.setRoomId(1L);
            request.setPlaceId(10L);
            request.setYearMonth("2026-03");

            given(rentalCalendarMapper.findPlaceClosedRules(eq(10L), eq(from), eq(to)))
                    .willReturn(Collections.emptyList());
            given(rentalCalendarMapper.findRoomUnavailableSlots(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .willReturn(Collections.emptyList());
            given(rentalCalendarMapper.findBasePricing(1L)).willReturn(Collections.emptyList());
            given(rentalCalendarMapper.findWeekendHolidayPricing(1L)).willReturn(Collections.emptyList());
            given(rentalCalendarMapper.findSpecialPricing(eq(1L), eq(from), eq(to)))
                    .willReturn(Collections.emptyList());
            given(rentalCalendarMapper.findReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .willReturn(Collections.emptyList());

            // when
            List<RentalCalendarDayResponse> result = calendarService.getCalendar(request);

            // then
            assertThat(result).hasSize(to.lengthOfMonth());
            assertThat(result)
                    .allSatisfy(day -> {
                        assertThat(day.getDate()).isNotBlank();
                        assertThat(day.getDayType()).isEqualTo("OPEN");
                    });
        }

        @Test
        @DisplayName("휴관 규칙에 해당하는 날짜는 CLOSED로 표시된다")
        void closedRule_marksDayAsClosed() {
            // given
            YearMonth ym = YearMonth.of(2026, 3);
            LocalDate from = ym.atDay(1);
            LocalDate to = ym.atEndOfMonth();
            LocalDate closedDate = ym.atDay(3);

            RentalCalendarRequest request = new RentalCalendarRequest();
            request.setRoomId(1L);
            request.setPlaceId(10L);
            request.setYearMonth("2026-03");

            Map<String, Object> closedRule = Map.of(
                    "rule_type", "DATE",
                    "start_date", java.sql.Date.valueOf(closedDate),
                    "end_date", java.sql.Date.valueOf(closedDate)
            );

            given(rentalCalendarMapper.findPlaceClosedRules(eq(10L), eq(from), eq(to)))
                    .willReturn(List.of(closedRule));
            given(rentalCalendarMapper.findRoomUnavailableSlots(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .willReturn(Collections.emptyList());
            given(rentalCalendarMapper.findBasePricing(1L)).willReturn(Collections.emptyList());
            given(rentalCalendarMapper.findWeekendHolidayPricing(1L)).willReturn(Collections.emptyList());
            given(rentalCalendarMapper.findSpecialPricing(eq(1L), eq(from), eq(to)))
                    .willReturn(Collections.emptyList());
            given(rentalCalendarMapper.findReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .willReturn(Collections.emptyList());

            // when
            List<RentalCalendarDayResponse> result = calendarService.getCalendar(request);

            // then
            RentalCalendarDayResponse closedDay = result.stream()
                    .filter(d -> d.getDate().equals(closedDate.toString()))
                    .findFirst()
                    .orElseThrow();

            assertThat(closedDay.getDayType()).isEqualTo("CLOSED");
            assertThat(closedDay.getSlots()).isEmpty();
        }
    }
}

