package com.nt.cms.rental.reservation.service;

import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.rental.reservation.constant.RentalReservationStatus;
import com.nt.cms.rental.reservation.dto.RentalReservationRequest;
import com.nt.cms.rental.reservation.dto.RentalReservationResponse;
import com.nt.cms.rental.reservation.dto.RentalReservationSearchRequest;
import com.nt.cms.rental.reservation.dto.RentalReservationSearchResponse;
import com.nt.cms.rental.reservation.mapper.RentalReservationMapper;
import com.nt.cms.rental.reservation.vo.RentalReservationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultRentalReservationService implements RentalReservationService {

    private final RentalReservationMapper rentalReservationMapper;

    @Override
    @Transactional
    public Long createReservation(RentalReservationRequest request, Long userId) {
        LocalDateTime start = parseDateTime(request.getStart());
        LocalDateTime end = parseDateTime(request.getEnd());

        if (!end.isAfter(start)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "종료 시간이 시작 시간보다 이후여야 합니다.");
        }

        // 기존 예약과의 충돌 여부만 1차 검증 (추후 휴관/요금 로직은 CalendarService에서 사용)
        List<RentalReservationVO> overlaps = rentalReservationMapper.findOverlappingReservations(
                request.getRoomId(), start, end);
        if (!overlaps.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "해당 시간대에는 이미 예약이 존재합니다.");
        }

        RentalReservationVO vo = RentalReservationVO.builder()
                .roomId(request.getRoomId())
                .userId(userId)
                .startDatetime(start)
                .endDatetime(end)
                .status(RentalReservationStatus.REQUESTED)
                .totalPrice(request.getTotalPrice() != null ? request.getTotalPrice() : 0L)
                .memo(request.getMemo())
                .createdAt(LocalDateTime.now())
                .createdBy(userId)
                .updatedAt(LocalDateTime.now())
                .updatedBy(userId)
                .deleted(false)
                .build();

        rentalReservationMapper.insert(vo);
        return vo.getId();
    }

    @Override
    public RentalReservationResponse getReservation(Long id, Long userId) {
        RentalReservationVO vo = rentalReservationMapper.findById(id);
        if (vo == null || Boolean.TRUE.equals(vo.getDeleted())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        if (!vo.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return toResponse(vo);
    }

    @Override
    public RentalReservationResponse getReservationByAdmin(Long id) {
        RentalReservationVO vo = rentalReservationMapper.findByIdWithUserName(id);
        if (vo == null || Boolean.TRUE.equals(vo.getDeleted())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return toResponse(vo);
    }

    @Override
    public List<RentalReservationResponse> getMyReservations(Long userId) {
        return rentalReservationMapper.findMyReservations(userId);
    }

    @Override
    public RentalReservationSearchResponse searchReservations(RentalReservationSearchRequest request) {
        List<RentalReservationResponse> items = rentalReservationMapper.search(request).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        Long totalAmount = rentalReservationMapper.sumTotalPriceBySearch(request);
        return RentalReservationSearchResponse.builder()
                .items(items)
                .totalAmount(totalAmount != null ? totalAmount : 0L)
                .build();
    }

    @Override
    @Transactional
    public void cancelByUser(Long id, Long userId) {
        int updated = rentalReservationMapper.cancelByUser(id, userId, LocalDateTime.now());
        if (updated == 0) {
            // 내 예약이 아니거나, 이미 삭제/취소된 경우
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void confirmByAdmin(Long id, Long actorId) {
        updateStatusByAdmin(id, actorId, RentalReservationStatus.CONFIRMED);
    }

    @Override
    @Transactional
    public void rejectByAdmin(Long id, Long actorId) {
        updateStatusByAdmin(id, actorId, RentalReservationStatus.REJECTED_BY_ADMIN);
    }

    @Override
    @Transactional
    public void cancelByAdmin(Long id, Long actorId) {
        updateStatusByAdmin(id, actorId, RentalReservationStatus.CANCELLED_BY_ADMIN);
    }

    private void updateStatusByAdmin(Long id, Long actorId, String status) {
        RentalReservationVO vo = rentalReservationMapper.findById(id);
        if (vo == null || Boolean.TRUE.equals(vo.getDeleted())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        vo.setStatus(status);
        vo.setUpdatedAt(LocalDateTime.now());
        vo.setUpdatedBy(actorId);
        rentalReservationMapper.update(vo);
    }

    private RentalReservationResponse toResponse(RentalReservationVO vo) {
        return RentalReservationResponse.builder()
                .id(vo.getId())
                .roomId(vo.getRoomId())
                .userId(vo.getUserId())
                .userName(vo.getUserName())
                .start(vo.getStartDatetime().toString())
                .end(vo.getEndDatetime().toString())
                .status(vo.getStatus())
                .totalPrice(vo.getTotalPrice())
                .memo(vo.getMemo())
                .build();
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "날짜/시간은 필수입니다.");
        }
        try {
            return OffsetDateTime.parse(value).toLocalDateTime();
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(value);
            } catch (DateTimeParseException ex) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 날짜/시간 형식입니다.");
            }
        }
    }
}

