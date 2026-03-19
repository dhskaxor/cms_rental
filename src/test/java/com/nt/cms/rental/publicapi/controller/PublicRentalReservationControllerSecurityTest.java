package com.nt.cms.rental.publicapi.controller;

import com.nt.cms.rental.reservation.service.RentalReservationService;
import com.nt.cms.test.support.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("PublicRentalReservationController 보안 테스트")
class PublicRentalReservationControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalReservationService rentalReservationService;

    @Test
    @DisplayName("미인증 사용자가 내 예약 조회 시 403을 반환한다")
    void getMyReservations_unauthenticated_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/public/rentals/reservations/my"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(userId = 1L)
    @DisplayName("인증 사용자가 내 예약 조회 시 200을 반환한다")
    void getMyReservations_authenticated_returns200() throws Exception {
        org.mockito.BDDMockito.given(rentalReservationService.getMyReservations(1L)).willReturn(List.of());

        mockMvc.perform(get("/api/v1/public/rentals/reservations/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}

