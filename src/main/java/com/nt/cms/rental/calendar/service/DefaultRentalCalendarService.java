package com.nt.cms.rental.calendar.service;

import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.rental.calendar.dto.ReservationSummary;
import com.nt.cms.rental.calendar.dto.RentalCalendarDayResponse;
import com.nt.cms.rental.calendar.dto.RentalCalendarRequest;
import com.nt.cms.rental.calendar.dto.RentalCalendarSlotResponse;
import com.nt.cms.rental.calendar.mapper.RentalCalendarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultRentalCalendarService implements RentalCalendarService {

    private final RentalCalendarMapper rentalCalendarMapper;

    @Override
    public List<RentalCalendarDayResponse> getCalendar(RentalCalendarRequest request) {
        if (request.getRoomId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "roomId는 필수입니다.");
        }
        if (request.getFromDate() == null || request.getToDate() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "fromDate/toDate는 필수입니다.");
        }

        LocalDate fromDate = LocalDate.parse(request.getFromDate());
        LocalDate toDate = LocalDate.parse(request.getToDate());
        if (toDate.isBefore(fromDate)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "toDate는 fromDate 이후여야 합니다.");
        }
        int slotMinutes = Optional.ofNullable(request.getSlotMinutes()).orElse(60);

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay(); // inclusive 날짜 범위

        // 휴관/대여불가/요금/예약 데이터 조회
        List<Map<String, Object>> closedRules = rentalCalendarMapper.findPlaceClosedRules(
                request.getPlaceId(), fromDate, toDate);
        List<Map<String, Object>> unavailableSlots = rentalCalendarMapper.findRoomUnavailableSlots(
                request.getRoomId(), fromDateTime, toDateTime);
        List<Map<String, Object>> basePricingList = rentalCalendarMapper.findBasePricing(request.getRoomId());
        List<Map<String, Object>> weekendHolidayPricingList = rentalCalendarMapper.findWeekendHolidayPricing(request.getRoomId());
        List<Map<String, Object>> specialPricingList = rentalCalendarMapper.findSpecialPricing(
                request.getRoomId(), fromDate, toDate);
        List<Map<String, Object>> reservations = rentalCalendarMapper.findReservations(
                request.getRoomId(), fromDateTime, toDateTime);

        Map<LocalDate, List<Map<String, Object>>> specialByDate = specialPricingList.stream()
                .collect(Collectors.groupingBy(m -> toLocalDate(m.get("date"))));

        List<RentalCalendarDayResponse> days = new ArrayList<>();
        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            DayOfWeek dow = date.getDayOfWeek();
            boolean isWeekend = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;

            String dayType = "OPEN";
            String holidayName = null;

            boolean isClosed = isClosedByRules(date, closedRules);
            if (isClosed) {
                dayType = "CLOSED";
                holidayName = findHolidayName(date, closedRules);
            }

            List<RentalCalendarSlotResponse> slots = new ArrayList<>();
            if (!isClosed) {
                LocalDateTime dayStart = date.atStartOfDay();
                LocalDateTime dayEnd = dayStart.plusDays(1);
                for (LocalDateTime slotStart = dayStart;
                     slotStart.isBefore(dayEnd);
                     slotStart = slotStart.plusMinutes(slotMinutes)) {

                    LocalDateTime slotEnd = slotStart.plusMinutes(slotMinutes);
                    if (slotEnd.isAfter(dayEnd)) {
                        break;
                    }

                    boolean available = true;
                    String reason = null;

                    if (isUnavailable(slotStart, slotEnd, unavailableSlots)) {
                        available = false;
                        reason = "UNAVAILABLE";
                    }
                    Map<String, Object> conflictReservation = findReservation(slotStart, slotEnd, reservations);
                    Long price = null;
                    String priceSource = null;

                    if (available) {
                        PriceResult priceResult = calculatePrice(slotStart.toLocalDate(), slotStart.toLocalTime(),
                                slotMinutes, isWeekend, specialByDate.getOrDefault(date, Collections.emptyList()),
                                weekendHolidayPricingList, basePricingList);
                        if (priceResult != null) {
                            price = priceResult.price();
                            priceSource = priceResult.source();
                        }
                    }

                    RentalCalendarSlotResponse.RentalCalendarSlotResponseBuilder builder =
                            RentalCalendarSlotResponse.builder()
                                    .start(slotStart.toString())
                                    .end(slotEnd.toString())
                                    .available(available)
                                    .reason(reason)
                                    .price(price)
                                    .priceSource(priceSource);

                    if (conflictReservation != null) {
                        builder.reservationId(((Number) conflictReservation.get("id")).longValue())
                                .reservationStatus((String) conflictReservation.get("status"))
                                .available(false)
                                .reason("RESERVED");
                    }

                    slots.add(builder.build());
                }
            }

            List<ReservationSummary> dayReservations = buildReservationsForDate(date, reservations);

            days.add(RentalCalendarDayResponse.builder()
                    .date(date.toString())
                    .dayType(dayType)
                    .holidayName(holidayName)
                    .slots(slots)
                    .reservations(dayReservations)
                    .build());
        }

        return days;
    }

    private List<ReservationSummary> buildReservationsForDate(LocalDate date, List<Map<String, Object>> reservations) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        return reservations.stream()
                .filter(row -> {
                    Date s = (Date) row.get("start_datetime");
                    Date e = (Date) row.get("end_datetime");
                    if (s == null || e == null) return false;
                    LocalDateTime rs = LocalDateTime.ofInstant(s.toInstant(), ZoneId.systemDefault());
                    LocalDateTime re = LocalDateTime.ofInstant(e.toInstant(), ZoneId.systemDefault());
                    return rs.isBefore(dayEnd) && re.isAfter(dayStart);
                })
                .map(row -> {
                    Date s = (Date) row.get("start_datetime");
                    Date e = (Date) row.get("end_datetime");
                    LocalDateTime rs = LocalDateTime.ofInstant(s.toInstant(), ZoneId.systemDefault());
                    LocalDateTime re = LocalDateTime.ofInstant(e.toInstant(), ZoneId.systemDefault());
                    String startTime = rs.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                    String endTime = re.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                    Long totalPrice = row.get("total_price") != null
                            ? ((Number) row.get("total_price")).longValue() : 0L;
                    return ReservationSummary.builder()
                            .id(((Number) row.get("id")).longValue())
                            .userName((String) row.get("user_name"))
                            .startTime(startTime)
                            .endTime(endTime)
                            .totalPrice(totalPrice)
                            .status((String) row.get("status"))
                            .build();
                })
                .sorted(Comparator.comparing(ReservationSummary::getStartTime))
                .collect(Collectors.toList());
    }

    private boolean isClosedByRules(LocalDate date, List<Map<String, Object>> rules) {
        for (Map<String, Object> rule : rules) {
            String ruleType = (String) rule.get("rule_type");
            if ("DATE".equals(ruleType)) {
                LocalDate s = toLocalDate(rule.get("start_date"));
                LocalDate e = Optional.ofNullable(toLocalDate(rule.get("end_date"))).orElse(s);
                if (!date.isBefore(s) && !date.isAfter(e)) {
                    return true;
                }
            } else if ("WEEKDAY".equals(ruleType)) {
                Number weekDay = (Number) rule.get("week_day");
                if (weekDay != null && weekDay.intValue() == date.getDayOfWeek().getValue()) {
                    return true;
                }
            } else if ("HOLIDAY".equals(ruleType)) {
                LocalDate s = toLocalDate(rule.get("start_date"));
                if (s != null) {
                    if (s.equals(date)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String findHolidayName(LocalDate date, List<Map<String, Object>> rules) {
        for (Map<String, Object> rule : rules) {
            String ruleType = (String) rule.get("rule_type");
            if (!"HOLIDAY".equals(ruleType)) {
                continue;
            }
            LocalDate s = toLocalDate(rule.get("start_date"));
            if (s != null) {
                if (s.equals(date)) {
                    return (String) rule.get("holiday_name");
                }
            }
        }
        return null;
    }

    private boolean isUnavailable(LocalDateTime slotStart, LocalDateTime slotEnd, List<Map<String, Object>> unavailableSlots) {
        for (Map<String, Object> row : unavailableSlots) {
            String scopeType = (String) row.get("scope_type");
            if ("DATE_RANGE".equals(scopeType) || "ONE_TIME".equals(scopeType)) {
                Date s = (Date) row.get("start_datetime");
                Date e = (Date) row.get("end_datetime");
                if (s == null || e == null) {
                    continue;
                }
                LocalDateTime rs = LocalDateTime.ofInstant(s.toInstant(), ZoneId.systemDefault());
                LocalDateTime re = LocalDateTime.ofInstant(e.toInstant(), ZoneId.systemDefault());
                if (rs.isBefore(slotEnd) && re.isAfter(slotStart)) {
                    return true;
                }
            } else if ("WEEKLY_TIME".equals(scopeType)) {
                Number weekDay = (Number) row.get("week_day");
                if (weekDay == null) {
                    continue;
                }
                if (slotStart.getDayOfWeek().getValue() != weekDay.intValue()) {
                    continue;
                }
                TimeRange tr = toTimeRange(row);
                if (tr != null && tr.overlaps(slotStart.toLocalTime(), slotEnd.toLocalTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<String, Object> findReservation(LocalDateTime slotStart, LocalDateTime slotEnd,
                                                List<Map<String, Object>> reservations) {
        for (Map<String, Object> row : reservations) {
            Date s = (Date) row.get("start_datetime");
            Date e = (Date) row.get("end_datetime");
            if (s == null || e == null) {
                continue;
            }
            LocalDateTime rs = LocalDateTime.ofInstant(s.toInstant(), ZoneId.systemDefault());
            LocalDateTime re = LocalDateTime.ofInstant(e.toInstant(), ZoneId.systemDefault());
            if (rs.isBefore(slotEnd) && re.isAfter(slotStart)) {
                return row;
            }
        }
        return null;
    }

    private PriceResult calculatePrice(LocalDate date,
                                       LocalTime slotStartTime,
                                       int slotMinutes,
                                       boolean isWeekend,
                                       List<Map<String, Object>> specialsForDate,
                                       List<Map<String, Object>> weekendHolidayPricingList,
                                       List<Map<String, Object>> basePricingList) {

        // 1) 특수 요금
        for (Map<String, Object> row : specialsForDate) {
            TimeRange tr = toSpecialTimeRange(row);
            if (tr != null && tr.contains(slotStartTime, slotMinutes)) {
                Long price = ((Number) row.get("price")).longValue();
                return new PriceResult(price, "SPECIAL");
            }
        }

        // 2) 주말/공휴일 요금
        for (Map<String, Object> row : weekendHolidayPricingList) {
            String applyTo = (String) row.get("apply_to");
            boolean applies = switch (applyTo) {
                case "WEEKEND" -> isWeekend;
                case "HOLIDAY" -> false; // 공휴일 판별 로직은 추후 확장
                case "BOTH" -> isWeekend;
                default -> false;
            };
            if (applies) {
                Long price = ((Number) row.get("price")).longValue();
                return new PriceResult(price, "WEEKEND_HOLIDAY");
            }
        }

        // 3) 기본 요금
        if (!basePricingList.isEmpty()) {
            Map<String, Object> row = basePricingList.get(0);
            Long price = ((Number) row.get("price")).longValue();
            return new PriceResult(price, "BASE");
        }

        return null;
    }

    private TimeRange toTimeRange(Map<String, Object> row) {
        java.sql.Time st = (java.sql.Time) row.get("start_time");
        java.sql.Time et = (java.sql.Time) row.get("end_time");
        if (st == null || et == null) {
            return null;
        }
        return new TimeRange(st.toLocalTime(), et.toLocalTime());
    }

    private TimeRange toSpecialTimeRange(Map<String, Object> row) {
        java.sql.Time st = (java.sql.Time) row.get("start_time");
        java.sql.Time et = (java.sql.Time) row.get("end_time");
        if (st == null || et == null) {
            return null;
        }
        return new TimeRange(st.toLocalTime(), et.toLocalTime());
    }

    private record TimeRange(LocalTime start, LocalTime end) {
        boolean overlaps(LocalTime slotStart, LocalTime slotEnd) {
            return start.isBefore(slotEnd) && end.isAfter(slotStart);
        }

        boolean contains(LocalTime slotStart, int minutes) {
            LocalTime slotEnd = slotStart.plusMinutes(minutes);
            return !slotStart.isBefore(start) && !slotEnd.isAfter(end);
        }
    }

    private record PriceResult(Long price, String source) {
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        if (value instanceof java.sql.Timestamp ts) {
            return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (value instanceof Date utilDate) {
            return utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (value instanceof LocalDate ld) {
            return ld;
        }
        if (value instanceof LocalDateTime ldt) {
            return ldt.toLocalDate();
        }
        throw new IllegalArgumentException("Unsupported date type: " + value.getClass());
    }
}

