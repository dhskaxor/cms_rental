package com.nt.cms.common.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ApiResponse 단위 테스트
 */
class ApiResponseTest {

    @Test
    @DisplayName("성공 응답 생성 - 데이터 포함")
    void success_withData() {
        // given
        String data = "테스트 데이터";
        
        // when
        ApiResponse<String> response = ApiResponse.success(data);
        
        // then
        assertTrue(response.isSuccess());
        assertEquals(data, response.getData());
        assertNull(response.getError());
    }

    @Test
    @DisplayName("성공 응답 생성 - 데이터 없음")
    void success_withoutData() {
        // when
        ApiResponse<Void> response = ApiResponse.success();
        
        // then
        assertTrue(response.isSuccess());
        assertNull(response.getData());
        assertNull(response.getError());
    }

    @Test
    @DisplayName("에러 응답 생성 - ErrorResponse 사용")
    void error_withErrorResponse() {
        // given
        ErrorResponse errorResponse = new ErrorResponse("TEST_ERROR", "테스트 에러");
        
        // when
        ApiResponse<Void> response = ApiResponse.error(errorResponse);
        
        // then
        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertNotNull(response.getError());
        assertEquals("TEST_ERROR", response.getError().getCode());
        assertEquals("테스트 에러", response.getError().getMessage());
    }

    @Test
    @DisplayName("에러 응답 생성 - 코드와 메시지 직접 지정")
    void error_withCodeAndMessage() {
        // when
        ApiResponse<Void> response = ApiResponse.error("ERROR_CODE", "에러 메시지");
        
        // then
        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertNotNull(response.getError());
        assertEquals("ERROR_CODE", response.getError().getCode());
        assertEquals("에러 메시지", response.getError().getMessage());
    }
}
