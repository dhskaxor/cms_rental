package com.nt.cms.rental.room.service;

import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.rental.room.dto.RentalRoomUnavailableSlotRequest;
import com.nt.cms.rental.room.dto.RentalRoomUnavailableSlotResponse;
import com.nt.cms.rental.room.mapper.RentalUnavailableSlotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultRentalUnavailableSlotService implements RentalUnavailableSlotService {

    private final RentalUnavailableSlotMapper rentalUnavailableSlotMapper;

    @Override
    public List<RentalRoomUnavailableSlotResponse> getSlots(Long roomId, String fromDateTime, String toDateTime) {
        if (roomId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "roomId는 필수입니다.");
        }
        LocalDateTime from = LocalDateTime.parse(fromDateTime);
        LocalDateTime to = LocalDateTime.parse(toDateTime);
        if (to.isBefore(from)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "to는 from 이후여야 합니다.");
        }
        return rentalUnavailableSlotMapper.findByRoomIdAndRange(roomId, from.toString(), to.toString());
    }

    @Override
    @Transactional
    public void createSlot(Long roomId, RentalRoomUnavailableSlotRequest request, Long actorId) {
        validateRequest(request);
        applyInsertOrUpdate(null, roomId, request, actorId, true);
    }

    @Override
    @Transactional
    public void updateSlot(Long id, Long roomId, RentalRoomUnavailableSlotRequest request, Long actorId) {
        if (id == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "id는 필수입니다.");
        }
        validateRequest(request);
        applyInsertOrUpdate(id, roomId, request, actorId, false);
    }

    @Override
    @Transactional
    public void deleteSlot(Long id, Long roomId, Long actorId) {
        if (id == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "id는 필수입니다.");
        }
        rentalUnavailableSlotMapper.softDelete(id, roomId, actorId);
    }

    private void validateRequest(RentalRoomUnavailableSlotRequest request) {
        if (request == null || request.getScopeType() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "scopeType은 필수입니다.");
        }
        String type = request.getScopeType();
        switch (type) {
            case "DATE_RANGE", "ONE_TIME" -> {
                if (request.getStartDateTime() == null || request.getEndDateTime() == null) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시작/종료 일시는 필수입니다.");
                }
                LocalDateTime s = LocalDateTime.parse(request.getStartDateTime());
                LocalDateTime e = LocalDateTime.parse(request.getEndDateTime());
                if (!e.isAfter(s)) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "종료 일시는 시작 일시 이후여야 합니다.");
                }
            }
            case "WEEKLY_TIME" -> {
                if (request.getWeekDay() == null || request.getWeekDay() < 1 || request.getWeekDay() > 7) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "weekDay는 1~7 이어야 합니다.");
                }
                if (request.getStartTime() == null || request.getEndTime() == null) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시작/종료 시간은 필수입니다.");
                }
            }
            default -> throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 scopeType입니다.");
        }
    }

    private void applyInsertOrUpdate(Long id,
                                     Long roomId,
                                     RentalRoomUnavailableSlotRequest request,
                                     Long actorId,
                                     boolean insert) {
        String scopeType = request.getScopeType();
        String startDateTime = null;
        String endDateTime = null;
        Integer weekDay = null;
        String startTime = null;
        String endTime = null;
        if ("DATE_RANGE".equals(scopeType) || "ONE_TIME".equals(scopeType)) {
            startDateTime = request.getStartDateTime();
            endDateTime = request.getEndDateTime();
        } else if ("WEEKLY_TIME".equals(scopeType)) {
            weekDay = request.getWeekDay();
            startTime = request.getStartTime();
            endTime = request.getEndTime();
        }
        if (insert) {
            rentalUnavailableSlotMapper.insert(
                    roomId,
                    scopeType,
                    startDateTime,
                    endDateTime,
                    weekDay,
                    startTime,
                    endTime,
                    request.getReason(),
                    actorId
            );
        } else {
            rentalUnavailableSlotMapper.update(
                    id,
                    roomId,
                    scopeType,
                    startDateTime,
                    endDateTime,
                    weekDay,
                    startTime,
                    endTime,
                    request.getReason(),
                    actorId
            );
        }
    }
}

