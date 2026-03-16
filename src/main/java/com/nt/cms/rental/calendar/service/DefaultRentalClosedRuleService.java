package com.nt.cms.rental.calendar.service;

import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.rental.calendar.dto.RentalPlaceClosedRuleRequest;
import com.nt.cms.rental.calendar.dto.RentalPlaceClosedRuleResponse;
import com.nt.cms.rental.calendar.mapper.RentalClosedRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultRentalClosedRuleService implements RentalClosedRuleService {

    private final RentalClosedRuleMapper rentalClosedRuleMapper;

    @Override
    public List<RentalPlaceClosedRuleResponse> getRules(Long placeId, String fromDate, String toDate) {
        if (placeId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "placeId는 필수입니다.");
        }
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        if (to.isBefore(from)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "toDate는 fromDate 이후여야 합니다.");
        }
        return rentalClosedRuleMapper.findByPlaceIdAndRange(placeId, from.toString(), to.toString());
    }

    @Override
    @Transactional
    public void createRule(Long placeId, RentalPlaceClosedRuleRequest request, Long actorId) {
        validateRequest(request);
        rentalClosedRuleMapper.insert(placeId, request, actorId);
    }

    @Override
    @Transactional
    public void updateRule(Long id, Long placeId, RentalPlaceClosedRuleRequest request, Long actorId) {
        if (id == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "id는 필수입니다.");
        }
        validateRequest(request);
        rentalClosedRuleMapper.update(id, placeId, request, actorId);
    }

    @Override
    @Transactional
    public void deleteRule(Long id, Long placeId, Long actorId) {
        if (id == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "id는 필수입니다.");
        }
        rentalClosedRuleMapper.softDelete(id, placeId, actorId);
    }

    private void validateRequest(RentalPlaceClosedRuleRequest request) {
        if (request == null || request.getRuleType() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "ruleType은 필수입니다.");
        }
        String type = request.getRuleType();
        switch (type) {
            case "WEEKDAY" -> {
                if (request.getWeekDay() == null || request.getWeekDay() < 1 || request.getWeekDay() > 7) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "WEEKDAY 규칙의 weekDay는 1~7 이어야 합니다.");
                }
            }
            case "DATE" -> {
                if (request.getStartDate() == null) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "DATE 규칙의 startDate는 필수입니다.");
                }
            }
            case "HOLIDAY" -> {
                if (request.getStartDate() == null) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "HOLIDAY 규칙의 startDate는 필수입니다.");
                }
                if (request.getHolidayName() == null || request.getHolidayName().isBlank()) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "HOLIDAY 규칙의 holidayName은 필수입니다.");
                }
            }
            default -> throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 ruleType입니다.");
        }
    }
}

