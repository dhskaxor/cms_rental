package com.nt.cms.rental.reservation.service;

import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.rental.reservation.dto.RentalReservationRequest;
import com.nt.cms.rental.reservation.dto.RentalReservationSearchRequest;
import com.nt.cms.rental.reservation.dto.RentalReservationSearchResponse;
import com.nt.cms.rental.reservation.mapper.RentalReservationMapper;
import com.nt.cms.rental.reservation.vo.RentalReservationVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultRentalReservationService 테스트")
class DefaultRentalReservationServiceTest {

    @Mock
    private RentalReservationMapper rentalReservationMapper;

    @InjectMocks
    private DefaultRentalReservationService reservationService;

    @Nested
    @DisplayName("예약 생성")
    class CreateReservationTest {

        @Test
        @DisplayName("정상 입력이면 예약이 생성된다")
        void createReservation_success() {
            // given
            RentalReservationRequest request = new RentalReservationRequest();
            request.setRoomId(1L);
            request.setStart("2026-03-01T10:00:00");
            request.setEnd("2026-03-01T11:00:00");

            given(rentalReservationMapper.findOverlappingReservations(any(), any(), any()))
                    .willReturn(Collections.emptyList());

            RentalReservationVO inserted = RentalReservationVO.builder()
                    .id(100L)
                    .roomId(1L)
                    .userId(1L)
                    .startDatetime(LocalDateTime.of(2026, 3, 1, 10, 0))
                    .endDatetime(LocalDateTime.of(2026, 3, 1, 11, 0))
                    .status("REQUESTED")
                    .deleted(false)
                    .build();

            org.mockito.Mockito.doAnswer(invocation -> {
                RentalReservationVO vo = invocation.getArgument(0);
                vo.setId(inserted.getId());
                return 1;
            }).when(rentalReservationMapper).insert(any(RentalReservationVO.class));

            // when
            Long id = reservationService.createReservation(request, 1L);

            // then
            assertThat(id).isEqualTo(100L);
            verify(rentalReservationMapper).findOverlappingReservations(eq(1L), any(), any());
            verify(rentalReservationMapper).insert(any(RentalReservationVO.class));
        }

        @Test
        @DisplayName("종료 시간이 시작 시간보다 빠르면 예외가 발생한다")
        void createReservation_invalidTimeOrder_throwsException() {
            // given
            RentalReservationRequest request = new RentalReservationRequest();
            request.setRoomId(1L);
            request.setStart("2026-03-01T11:00:00");
            request.setEnd("2026-03-01T10:00:00");

            // when & then
            assertThatThrownBy(() -> reservationService.createReservation(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
        }

        @Test
        @DisplayName("겹치는 예약이 있으면 예외가 발생한다")
        void createReservation_overlap_throwsException() {
            // given
            RentalReservationRequest request = new RentalReservationRequest();
            request.setRoomId(1L);
            request.setStart("2026-03-01T10:00:00");
            request.setEnd("2026-03-01T11:00:00");

            given(rentalReservationMapper.findOverlappingReservations(any(), any(), any()))
                    .willReturn(List.of(RentalReservationVO.builder().id(1L).build()));

            // when & then
            assertThatThrownBy(() -> reservationService.createReservation(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    @Nested
    @DisplayName("예약 검색 합계")
    class SearchReservationTest {

        @Test
        @DisplayName("검색 결과의 총 금액이 null이면 0으로 반환한다")
        void searchReservations_nullTotalAmount_returnsZero() {
            // given
            RentalReservationSearchRequest request = new RentalReservationSearchRequest();

            RentalReservationVO vo = RentalReservationVO.builder()
                    .id(1L)
                    .roomId(1L)
                    .userId(1L)
                    .startDatetime(LocalDateTime.of(2026, 3, 1, 10, 0))
                    .endDatetime(LocalDateTime.of(2026, 3, 1, 11, 0))
                    .status("CONFIRMED")
                    .totalPrice(10000L)
                    .deleted(false)
                    .build();

            given(rentalReservationMapper.search(request)).willReturn(List.of(vo));
            given(rentalReservationMapper.sumTotalPriceBySearch(request)).willReturn(null);

            // when
            RentalReservationSearchResponse response = reservationService.searchReservations(request);

            // then
            assertThat(response.getItems()).hasSize(1);
            assertThat(response.getTotalAmount()).isZero();
        }
    }
}

