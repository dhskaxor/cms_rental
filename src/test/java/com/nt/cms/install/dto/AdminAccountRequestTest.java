package com.nt.cms.install.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdminAccountRequest 테스트
 * 
 * @author CMS Team
 */
class AdminAccountRequestTest {

    @Test
    @DisplayName("비밀번호가 일치하면 true를 반환해야 한다")
    void isPasswordMatch_whenMatch() {
        // given
        AdminAccountRequest request = new AdminAccountRequest();
        request.setPassword("Test123!");
        request.setPasswordConfirm("Test123!");

        // when
        boolean result = request.isPasswordMatch();

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 false를 반환해야 한다")
    void isPasswordMatch_whenNotMatch() {
        // given
        AdminAccountRequest request = new AdminAccountRequest();
        request.setPassword("Test123!");
        request.setPasswordConfirm("Test456!");

        // when
        boolean result = request.isPasswordMatch();

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("비밀번호가 null이면 false를 반환해야 한다")
    void isPasswordMatch_whenPasswordNull() {
        // given
        AdminAccountRequest request = new AdminAccountRequest();
        request.setPassword(null);
        request.setPasswordConfirm("Test123!");

        // when
        boolean result = request.isPasswordMatch();

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("확인 비밀번호가 null이면 false를 반환해야 한다")
    void isPasswordMatch_whenConfirmNull() {
        // given
        AdminAccountRequest request = new AdminAccountRequest();
        request.setPassword("Test123!");
        request.setPasswordConfirm(null);

        // when
        boolean result = request.isPasswordMatch();

        // then
        assertFalse(result);
    }
}
