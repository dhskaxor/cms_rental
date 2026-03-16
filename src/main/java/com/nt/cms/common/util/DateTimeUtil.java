package com.nt.cms.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 날짜/시간 유틸리티
 * 
 * <p>날짜 및 시간 관련 공통 기능을 제공한다.</p>
 * 
 * @author CMS Team
 */
public final class DateTimeUtil {

    /**
     * 기본 날짜 포맷 (yyyy-MM-dd)
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * 기본 시간 포맷 (HH:mm:ss)
     */
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    /**
     * 기본 날짜시간 포맷 (yyyy-MM-dd HH:mm:ss)
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 날짜 포맷터
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    
    /**
     * 시간 포맷터
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    
    /**
     * 날짜시간 포맷터
     */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    /**
     * 인스턴스 생성 방지
     */
    private DateTimeUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 현재 날짜시간 반환
     * 
     * @return 현재 LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 현재 날짜 반환
     * 
     * @return 현재 LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * LocalDateTime을 문자열로 변환
     * 
     * @param dateTime 변환할 날짜시간
     * @return 포맷팅된 문자열
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * LocalDate를 문자열로 변환
     * 
     * @param date 변환할 날짜
     * @return 포맷팅된 문자열
     */
    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * 문자열을 LocalDateTime으로 변환
     * 
     * @param dateTimeStr 날짜시간 문자열
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /**
     * 문자열을 LocalDate로 변환
     * 
     * @param dateStr 날짜 문자열
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 두 날짜 사이의 일수 계산
     * 
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 일수
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 지정된 일수만큼 더한 날짜 반환
     * 
     * @param date 기준 날짜
     * @param days 더할 일수
     * @return 결과 날짜
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        return date.plusDays(days);
    }

    /**
     * 지정된 시간(초)만큼 더한 날짜시간 반환
     * 
     * @param dateTime 기준 날짜시간
     * @param seconds 더할 초
     * @return 결과 날짜시간
     */
    public static LocalDateTime plusSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime.plusSeconds(seconds);
    }

    /**
     * 오늘 시작 시간 (00:00:00) 반환
     * 
     * @return 오늘 시작 시간
     */
    public static LocalDateTime startOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    /**
     * 오늘 종료 시간 (23:59:59) 반환
     * 
     * @return 오늘 종료 시간
     */
    public static LocalDateTime endOfToday() {
        return LocalDate.now().atTime(23, 59, 59);
    }

    /**
     * 지정된 날짜의 시작 시간 반환
     * 
     * @param date 날짜
     * @return 해당 날짜 시작 시간
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 지정된 날짜의 종료 시간 반환
     * 
     * @param date 날짜
     * @return 해당 날짜 종료 시간
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }
}
