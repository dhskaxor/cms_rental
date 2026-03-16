package com.nt.cms.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PasswordUtil 단위 테스트
 */
class PasswordUtilTest {

    @Test
    @DisplayName("유효한 비밀번호 검증")
    void isValid_validPassword() {
        // 영문, 숫자, 특수문자 포함, 6~12자
        assertTrue(PasswordUtil.isValid("Test1!"));
        assertTrue(PasswordUtil.isValid("Password1@"));
        assertTrue(PasswordUtil.isValid("Abc123!@#"));
        assertTrue(PasswordUtil.isValid("P@ssw0rd12"));
    }

    @Test
    @DisplayName("비밀번호 길이 미달")
    void isValid_tooShort() {
        assertFalse(PasswordUtil.isValid("Te1!")); // 4자
        assertFalse(PasswordUtil.isValid("Ab1@")); // 4자
    }

    @Test
    @DisplayName("비밀번호 길이 초과")
    void isValid_tooLong() {
        assertFalse(PasswordUtil.isValid("TestPassword1@")); // 14자
        assertFalse(PasswordUtil.isValid("VeryLongPass1!")); // 14자
    }

    @ParameterizedTest
    @DisplayName("영문 미포함")
    @ValueSource(strings = {"123456!", "1234@#", "12345678!"})
    void isValid_noLetter(String password) {
        assertFalse(PasswordUtil.isValid(password));
    }

    @ParameterizedTest
    @DisplayName("숫자 미포함")
    @ValueSource(strings = {"Abcdef!", "Test@#$", "Password!"})
    void isValid_noDigit(String password) {
        assertFalse(PasswordUtil.isValid(password));
    }

    @ParameterizedTest
    @DisplayName("특수문자 미포함")
    @ValueSource(strings = {"Abcdef1", "Test123", "Password1"})
    void isValid_noSpecialChar(String password) {
        assertFalse(PasswordUtil.isValid(password));
    }

    @Test
    @DisplayName("null 또는 빈 문자열")
    void isValid_nullOrEmpty() {
        assertFalse(PasswordUtil.isValid(null));
        assertFalse(PasswordUtil.isValid(""));
        assertFalse(PasswordUtil.isValid("   "));
    }

    @Test
    @DisplayName("위반 사유 반환 - 정책 준수")
    void getViolationReason_valid() {
        assertNull(PasswordUtil.getViolationReason("Test1!"));
    }

    @Test
    @DisplayName("위반 사유 반환 - 길이 미달")
    void getViolationReason_tooShort() {
        String reason = PasswordUtil.getViolationReason("Te1!");
        assertNotNull(reason);
        assertTrue(reason.contains("최소"));
    }

    @Test
    @DisplayName("위반 사유 반환 - 영문 미포함")
    void getViolationReason_noLetter() {
        String reason = PasswordUtil.getViolationReason("123456!");
        assertNotNull(reason);
        assertTrue(reason.contains("영문"));
    }

    @Test
    @DisplayName("위반 사유 반환 - 숫자 미포함")
    void getViolationReason_noDigit() {
        String reason = PasswordUtil.getViolationReason("Abcdef!");
        assertNotNull(reason);
        assertTrue(reason.contains("숫자"));
    }

    @Test
    @DisplayName("위반 사유 반환 - 특수문자 미포함")
    void getViolationReason_noSpecial() {
        String reason = PasswordUtil.getViolationReason("Abcdef1");
        assertNotNull(reason);
        assertTrue(reason.contains("특수문자"));
    }
}
