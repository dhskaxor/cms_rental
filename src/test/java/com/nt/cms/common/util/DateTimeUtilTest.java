package com.nt.cms.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateTimeUtil 단위 테스트
 */
class DateTimeUtilTest {

    @Test
    @DisplayName("현재 날짜시간 반환")
    void now() {
        LocalDateTime now = DateTimeUtil.now();
        assertNotNull(now);
    }

    @Test
    @DisplayName("현재 날짜 반환")
    void today() {
        LocalDate today = DateTimeUtil.today();
        assertNotNull(today);
        assertEquals(LocalDate.now(), today);
    }

    @Test
    @DisplayName("LocalDateTime 포맷팅")
    void format_dateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 2, 22, 10, 30, 45);
        String formatted = DateTimeUtil.format(dateTime);
        assertEquals("2026-02-22 10:30:45", formatted);
    }

    @Test
    @DisplayName("LocalDate 포맷팅")
    void format_date() {
        LocalDate date = LocalDate.of(2026, 2, 22);
        String formatted = DateTimeUtil.format(date);
        assertEquals("2026-02-22", formatted);
    }

    @Test
    @DisplayName("null 포맷팅")
    void format_null() {
        assertNull(DateTimeUtil.format((LocalDateTime) null));
        assertNull(DateTimeUtil.format((LocalDate) null));
    }

    @Test
    @DisplayName("문자열을 LocalDateTime으로 파싱")
    void parseDateTime() {
        String dateTimeStr = "2026-02-22 10:30:45";
        LocalDateTime dateTime = DateTimeUtil.parseDateTime(dateTimeStr);
        
        assertNotNull(dateTime);
        assertEquals(2026, dateTime.getYear());
        assertEquals(2, dateTime.getMonthValue());
        assertEquals(22, dateTime.getDayOfMonth());
        assertEquals(10, dateTime.getHour());
        assertEquals(30, dateTime.getMinute());
        assertEquals(45, dateTime.getSecond());
    }

    @Test
    @DisplayName("문자열을 LocalDate로 파싱")
    void parseDate() {
        String dateStr = "2026-02-22";
        LocalDate date = DateTimeUtil.parseDate(dateStr);
        
        assertNotNull(date);
        assertEquals(2026, date.getYear());
        assertEquals(2, date.getMonthValue());
        assertEquals(22, date.getDayOfMonth());
    }

    @Test
    @DisplayName("null/빈 문자열 파싱")
    void parse_nullOrEmpty() {
        assertNull(DateTimeUtil.parseDateTime(null));
        assertNull(DateTimeUtil.parseDateTime(""));
        assertNull(DateTimeUtil.parseDateTime("   "));
        
        assertNull(DateTimeUtil.parseDate(null));
        assertNull(DateTimeUtil.parseDate(""));
    }

    @Test
    @DisplayName("두 날짜 사이의 일수 계산")
    void daysBetween() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        
        assertEquals(30, DateTimeUtil.daysBetween(start, end));
    }

    @Test
    @DisplayName("날짜에 일수 더하기")
    void plusDays() {
        LocalDate date = LocalDate.of(2026, 2, 22);
        LocalDate result = DateTimeUtil.plusDays(date, 10);
        
        assertEquals(LocalDate.of(2026, 3, 4), result);
    }

    @Test
    @DisplayName("날짜시간에 초 더하기")
    void plusSeconds() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 2, 22, 10, 0, 0);
        LocalDateTime result = DateTimeUtil.plusSeconds(dateTime, 3600);
        
        assertEquals(LocalDateTime.of(2026, 2, 22, 11, 0, 0), result);
    }

    @Test
    @DisplayName("오늘 시작/종료 시간")
    void startAndEndOfToday() {
        LocalDateTime startOfToday = DateTimeUtil.startOfToday();
        LocalDateTime endOfToday = DateTimeUtil.endOfToday();
        
        assertEquals(0, startOfToday.getHour());
        assertEquals(0, startOfToday.getMinute());
        assertEquals(0, startOfToday.getSecond());
        
        assertEquals(23, endOfToday.getHour());
        assertEquals(59, endOfToday.getMinute());
        assertEquals(59, endOfToday.getSecond());
    }

    @Test
    @DisplayName("지정 날짜 시작/종료 시간")
    void startAndEndOfDay() {
        LocalDate date = LocalDate.of(2026, 2, 22);
        
        LocalDateTime startOfDay = DateTimeUtil.startOfDay(date);
        LocalDateTime endOfDay = DateTimeUtil.endOfDay(date);
        
        assertEquals(LocalDateTime.of(2026, 2, 22, 0, 0, 0), startOfDay);
        assertEquals(LocalDateTime.of(2026, 2, 22, 23, 59, 59), endOfDay);
    }
}
