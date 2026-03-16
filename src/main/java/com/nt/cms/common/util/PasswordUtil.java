package com.nt.cms.common.util;

import java.util.regex.Pattern;

/**
 * 비밀번호 유틸리티
 * 
 * <p>비밀번호 정책 검증 및 관련 기능을 제공한다.</p>
 * <p>비밀번호 정책: 영문/숫자/특수문자 포함, 최소 6자 최대 12자</p>
 * 
 * @author CMS Team
 */
public final class PasswordUtil {

    /**
     * 영문 포함 패턴
     */
    private static final Pattern HAS_LETTER = Pattern.compile("[a-zA-Z]");
    
    /**
     * 숫자 포함 패턴
     */
    private static final Pattern HAS_DIGIT = Pattern.compile("[0-9]");
    
    /**
     * 특수문자 포함 패턴
     */
    private static final Pattern HAS_SPECIAL = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    /**
     * 인스턴스 생성 방지
     */
    private PasswordUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 비밀번호 정책 검증
     * 
     * @param password 검증할 비밀번호
     * @param minLength 최소 길이
     * @param maxLength 최대 길이
     * @return 정책 준수 여부
     */
    public static boolean isValid(String password, int minLength, int maxLength) {
        if (password == null || password.isBlank()) {
            return false;
        }
        
        int length = password.length();
        
        // 길이 검증
        if (length < minLength || length > maxLength) {
            return false;
        }
        
        // 영문 포함 여부
        if (!HAS_LETTER.matcher(password).find()) {
            return false;
        }
        
        // 숫자 포함 여부
        if (!HAS_DIGIT.matcher(password).find()) {
            return false;
        }
        
        // 특수문자 포함 여부
        if (!HAS_SPECIAL.matcher(password).find()) {
            return false;
        }
        
        return true;
    }

    /**
     * 기본 정책으로 비밀번호 검증 (6~12자)
     * 
     * @param password 검증할 비밀번호
     * @return 정책 준수 여부
     */
    public static boolean isValid(String password) {
        return isValid(password, 6, 12);
    }

    /**
     * 비밀번호 정책 위반 사유 반환
     * 
     * @param password 검증할 비밀번호
     * @param minLength 최소 길이
     * @param maxLength 최대 길이
     * @return 위반 사유 (정책 준수 시 null)
     */
    public static String getViolationReason(String password, int minLength, int maxLength) {
        if (password == null || password.isBlank()) {
            return "비밀번호를 입력해주세요.";
        }
        
        int length = password.length();
        
        if (length < minLength) {
            return String.format("비밀번호는 최소 %d자 이상이어야 합니다.", minLength);
        }
        
        if (length > maxLength) {
            return String.format("비밀번호는 최대 %d자까지 가능합니다.", maxLength);
        }
        
        if (!HAS_LETTER.matcher(password).find()) {
            return "비밀번호에 영문이 포함되어야 합니다.";
        }
        
        if (!HAS_DIGIT.matcher(password).find()) {
            return "비밀번호에 숫자가 포함되어야 합니다.";
        }
        
        if (!HAS_SPECIAL.matcher(password).find()) {
            return "비밀번호에 특수문자가 포함되어야 합니다.";
        }
        
        return null; // 정책 준수
    }

    /**
     * 기본 정책으로 위반 사유 반환
     * 
     * @param password 검증할 비밀번호
     * @return 위반 사유 (정책 준수 시 null)
     */
    public static String getViolationReason(String password) {
        return getViolationReason(password, 6, 12);
    }
}
