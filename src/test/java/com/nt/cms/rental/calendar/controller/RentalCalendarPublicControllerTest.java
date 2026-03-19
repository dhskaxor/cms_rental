package com.nt.cms.rental.calendar.controller;

import com.nt.cms.common.exception.GlobalExceptionHandler;
import com.nt.cms.rental.calendar.dto.RentalCalendarDayResponse;
import com.nt.cms.rental.calendar.service.RentalCalendarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RentalCalendarPublicController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class RentalCalendarPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalCalendarService rentalCalendarService;

    @Nested
    @DisplayName("GET /api/v1/rental/search")
    class SearchAvailableSlots {

        @Test
        @DisplayName("roomId, yearMonth가 있으면 200과 일 목록을 반환한다")
        void search_withValidParams_returns200() throws Exception {
            List<RentalCalendarDayResponse> days = Collections.emptyList();
            when(rentalCalendarService.getCalendar(any())).thenReturn(days);

            mockMvc.perform(get("/api/v1/rental/search")
                            .param("roomId", "1")
                            .param("yearMonth", "2026-03"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        @DisplayName("roomId가 없으면 400 에러를 반환한다")
        void search_withoutRoomId_returns400() throws Exception {
            when(rentalCalendarService.getCalendar(any())).thenThrow(
                    new com.nt.cms.common.exception.BusinessException(
                            com.nt.cms.common.exception.ErrorCode.INVALID_INPUT_VALUE, "roomId는 필수입니다."));

            mockMvc.perform(get("/api/v1/rental/search")
                            .param("yearMonth", "2026-03"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
